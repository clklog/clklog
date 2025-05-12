package com.zcunsoft.clklog.manage.services;

import com.zcunsoft.clklog.common.exception.ServiceException;
import com.zcunsoft.clklog.common.model.LoginUser;
import com.zcunsoft.clklog.common.utils.ObjectMapperUtil;
import com.zcunsoft.clklog.common.utils.SecurityUtils;
import com.zcunsoft.clklog.manage.cfg.ClklogManageSetting;
import com.zcunsoft.clklog.manage.cfg.RedisConstsConfig;
import com.zcunsoft.clklog.manage.entity.mysql.TblGlobalSetting;
import com.zcunsoft.clklog.manage.entity.mysql.TblProject;
import com.zcunsoft.clklog.manage.entity.mysql.TblProjectLogStat;
import com.zcunsoft.clklog.manage.entity.mysql.TblProjectStat;
import com.zcunsoft.clklog.manage.handlers.ConstsDataHolder;
import com.zcunsoft.clklog.manage.models.ProjectSetting;
import com.zcunsoft.clklog.manage.models.enums.ProjectStatus;
import com.zcunsoft.clklog.manage.models.global.*;
import com.zcunsoft.clklog.manage.models.project.*;
import com.zcunsoft.clklog.manage.repository.mysql.GlobalSettingRepository;
import com.zcunsoft.clklog.manage.repository.mysql.ProjectLogStatRepository;
import com.zcunsoft.clklog.manage.repository.mysql.ProjectRepository;
import com.zcunsoft.clklog.manage.repository.mysql.ProjectStatRepository;
import com.zcunsoft.clklog.manage.utils.ExtensionFilter;
import com.zcunsoft.clklog.manage.utils.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理服务
 */
@Service
public class ManageServiceImpl implements IManageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ConstsDataHolder constsDataHolder;

    private final GlobalSettingRepository globalSettingRepository;

    private final ProjectRepository projectRepository;

    private final ProjectStatRepository projectStatRepository;

    private final ProjectLogStatRepository projectLogStatRepository;

    private final ClklogManageSetting clklogManageSetting;

    private final NamedParameterJdbcTemplate clickHouseJdbcTemplate;

    private final ObjectMapperUtil objectMapper;

    private final StringRedisTemplate redisTemplate;

    private final RedisConstsConfig redisConstsConfig;

    private final ThreadLocal<DateFormat> yMhFORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd"));

    private final ThreadLocal<DateFormat> y_M_hFORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

    public ManageServiceImpl(ConstsDataHolder constsDataHolder, GlobalSettingRepository globalSettingRepository, ProjectRepository projectRepository, ProjectStatRepository projectStatRepository, ProjectLogStatRepository projectLogStatRepository, ClklogManageSetting clklogManageSetting, NamedParameterJdbcTemplate clickHouseJdbcTemplate,
                             ObjectMapperUtil objectMapper, StringRedisTemplate redisTemplate, RedisConstsConfig redisConstsConfig) {
        this.constsDataHolder = constsDataHolder;
        this.globalSettingRepository = globalSettingRepository;
        this.projectRepository = projectRepository;
        this.projectStatRepository = projectStatRepository;
        this.projectLogStatRepository = projectLogStatRepository;
        this.clklogManageSetting = clklogManageSetting;
        this.clickHouseJdbcTemplate = clickHouseJdbcTemplate;
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
        this.redisConstsConfig = redisConstsConfig;
    }

    @Override
    public SaveSettingResponse saveSetting(SaveGlobalSettingRequest saveGlobalSettingRequest) {
        Specification<TblGlobalSetting> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            Predicate[] pre = new Predicate[predicates.size()];
            return cb.and(predicates.toArray(pre));
        };

        List<TblGlobalSetting> globalSettingList = this.globalSettingRepository.findAll(spec);
        TblGlobalSetting globalSetting = null;
        if (globalSettingList.size() > 0) {
            globalSetting = globalSettingList.get(0);
        } else {
            globalSetting = new TblGlobalSetting();
            globalSetting.setId(UUID.randomUUID().toString());
        }
        globalSetting.setExcludedIp(saveGlobalSettingRequest.getExcludedIp());
        globalSetting.setExcludedUa(saveGlobalSettingRequest.getExcludedUa());
        globalSetting.setExcludedUrlParams(saveGlobalSettingRequest.getExcludedUrlParams());
        globalSetting.setSearchwordKey(saveGlobalSettingRequest.getSearchwordKey());
        globalSetting.setSearchwordCategoryKey(saveGlobalSettingRequest.getSearchwordCategoryKey());
        globalSetting.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        globalSettingRepository.save(globalSetting);

        return new SaveSettingResponse();
    }

    @Override
    public GetSettingResponse getSetting() {
        Specification<TblGlobalSetting> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            Predicate[] pre = new Predicate[predicates.size()];
            return cb.and(predicates.toArray(pre));
        };

        List<TblGlobalSetting> globalSettingList = this.globalSettingRepository.findAll(spec);

        GetSettingResponse getSettingResponse = new GetSettingResponse();
        GlobalSettingDto settingDto = new GlobalSettingDto();
        if (globalSettingList.size() > 0) {
            TblGlobalSetting setting = globalSettingList.get(0);
            settingDto.setExcludedIp(setting.getExcludedIp());
            settingDto.setExcludedUa(setting.getExcludedUa());
            settingDto.setExcludedUrlParams(setting.getExcludedUrlParams());
            settingDto.setSearchwordKey(setting.getSearchwordKey());
            settingDto.setSearchwordCategoryKey(setting.getSearchwordCategoryKey());
        }

        getSettingResponse.setData(settingDto);
        return getSettingResponse;
    }

    @Override
    public GetProjectResponse getProject(GetProjectRequest getProjectRequest) {
        GetProjectResponse response = new GetProjectResponse();
        try {
            Specification<TblProject> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(cb.equal(root.get("id"), getProjectRequest.getId()));
                predicates.add(cb.notEqual(root.get("status"), ProjectStatus.Deleted.getName()));
                Predicate[] pre = new Predicate[predicates.size()];
                return cb.and(predicates.toArray(pre));
            };

            List<TblProject> tblProjectList = this.projectRepository.findAll(spec);
            if (tblProjectList.isEmpty()) {
                response.setCode(500);
                response.setMsg("项目不存在");
            } else {
                TblProject tblProject = tblProjectList.get(0);
                Project project = new Project();
                project.setId(tblProject.getId());
                project.setProjectName(tblProject.getProjectName());
                project.setProjectDisplayName(tblProject.getProjectDisplayName());
                project.setExcludedIp(tblProject.getExcludedIp());
                project.setExcludedUa(tblProject.getExcludedUa());
                project.setExcludedUrlParams(tblProject.getExcludedUrlParams());
                project.setSearchwordKey(tblProject.getSearchwordKey());
                project.setSearchwordCategoryKey(tblProject.getSearchwordCategoryKey());
                project.setRootUrls(tblProject.getRootUrls());
                response.setData(project);
            }
        } catch (Exception ex) {
            logger.error("get project error", ex);
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public AddProjectResponse addProject(AddProjectRequest addProjectRequest) {
        AddProjectResponse response = new AddProjectResponse();
        try {
            String err = "";
            if ("all".equalsIgnoreCase(addProjectRequest.getProjectName())) {
                err = "项目编码不允许为all";
            }
            if (err.isEmpty()) {
                Specification<TblProject> spec = (root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<Predicate>();
                    predicates.add(cb.notEqual(root.get("status"), ProjectStatus.Deleted.getName()));
                    predicates.add(cb.equal(root.get("projectName"), addProjectRequest.getProjectName()));
                    Predicate[] pre = new Predicate[predicates.size()];
                    return cb.and(predicates.toArray(pre));
                };

                List<TblProject> projectList = this.projectRepository.findAll(spec);
                if (!projectList.isEmpty()) {
                    err = addProjectRequest.getProjectName() + "项目已存在";
                }
            }
            if (err.isEmpty()) {
                TblProject tblProject = new TblProject();
                tblProject.setId(UUID.randomUUID().toString());
                tblProject.setProjectName(addProjectRequest.getProjectName());
                tblProject.setProjectDisplayName(addProjectRequest.getProjectDisplayName());
                tblProject.setExcludedIp(addProjectRequest.getExcludedIp());
                tblProject.setExcludedUa(addProjectRequest.getExcludedUa());
                tblProject.setExcludedUrlParams(addProjectRequest.getExcludedUrlParams());
                tblProject.setSearchwordKey(addProjectRequest.getSearchwordKey());
                tblProject.setSearchwordCategoryKey(addProjectRequest.getSearchwordCategoryKey());
                tblProject.setToken(UUID.randomUUID().toString());
                tblProject.setCreateTime(new Timestamp(System.currentTimeMillis()));
                tblProject.setStatus(ProjectStatus.Saved.getName());
                tblProject.setRootUrls(addProjectRequest.getRootUrls());
                projectRepository.save(tblProject);
                AddProjectResponseData responseData = new AddProjectResponseData();
                responseData.setId(tblProject.getId());
                response.setData(responseData);
            } else {
                response.setCode(500);
                response.setMsg(err);
            }
        } catch (Exception ex) {
            logger.error("add project error,", ex);
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public void editProject(EditProjectRequest editProjectRequest) {
//		try {
        Specification<TblProject> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(root.get("id"), editProjectRequest.getId()));
            predicates.add(cb.notEqual(root.get("status"), ProjectStatus.Deleted.getName()));
            Predicate[] pre = new Predicate[predicates.size()];
            return cb.and(predicates.toArray(pre));
        };
        TblProject tblProject = this.projectRepository.findOne(spec)
                .orElseThrow(() -> new ServiceException(editProjectRequest.getId() + "项目不存在", 500));

        tblProject.setProjectDisplayName(editProjectRequest.getProjectDisplayName());
        tblProject.setExcludedIp(editProjectRequest.getExcludedIp());
        tblProject.setExcludedUa(editProjectRequest.getExcludedUa());
        tblProject.setExcludedUrlParams(editProjectRequest.getExcludedUrlParams());
        tblProject.setSearchwordKey(editProjectRequest.getSearchwordKey());
        tblProject.setSearchwordCategoryKey(editProjectRequest.getSearchwordCategoryKey());
        tblProject.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        tblProject.setRootUrls(editProjectRequest.getRootUrls());
        projectRepository.save(tblProject);
//		} catch (Exception ex) {
//			logger.error("edit project error," + ex.getMessage());
//			throw new ServiceException("操作失败", 500);
//		}
    }

    @Override
    public void deleteProject(DeleteProjectRequest delProjectRequest) {
//		try {
        Specification<TblProject> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(root.get("id"), delProjectRequest.getId()));
            Predicate[] pre = new Predicate[predicates.size()];
            return cb.and(predicates.toArray(pre));
        };
        List<TblProject> tblProjectList = this.projectRepository.findAll(spec);
        if (tblProjectList.isEmpty()) {
            throw new ServiceException(delProjectRequest.getId() + "项目不存在", 500);
        } else {
            TblProject tblProject = tblProjectList.get(0);
            tblProject.setStatus(ProjectStatus.Deleted.getName());
            projectRepository.save(tblProject);
        }
//		} catch (Exception ex) {
//			logger.error("delete project error," + ex.getMessage());
//			throw new ServiceException("操作失败", 500);
//		}
    }

    @Override
    public QueryProjectResponse getProjectPageList(QueryProjectRequest queryProjectRequest) {
        QueryProjectResponse response = new QueryProjectResponse();
        try {
            Sort sort = Sort.by(Sort.Direction.ASC, "createTime");

            Pageable pageable = PageRequest.of(queryProjectRequest.getPageNum() - 1, queryProjectRequest.getPageSize(), sort);

            Specification<TblProject> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(cb.notEqual(root.get("status"), ProjectStatus.Deleted.getName()));
                Predicate[] pre = new Predicate[predicates.size()];
                return cb.and(predicates.toArray(pre));
            };
            Page<TblProject> projectPageList = this.projectRepository.findAll(spec, pageable);
            List<TblProject> tblProjectList = projectPageList.getContent();

            List<ProjectStat> projectList = null;
            if (queryProjectRequest.getEndDate() == null && queryProjectRequest.getStartDate() == null) {
                projectList = getTblProjectList(tblProjectList.stream().map(m -> m.getProjectName()).collect(Collectors.toList()));
            } else {
                projectList = statProject(tblProjectList, queryProjectRequest.getStartDate(), queryProjectRequest.getEndDate(), null);
            }

            QueryProjectResponseData responseData = new QueryProjectResponseData();

            List<ProjectSlim> projectSlimList = new ArrayList<ProjectSlim>();

            for (TblProject tblProject : tblProjectList) {
                ProjectSlim projectSlim = new ProjectSlim();
                projectSlim.setId(tblProject.getId());
                projectSlim.setProjectDisplayName(tblProject.getProjectDisplayName());
                projectSlim.setProjectName(tblProject.getProjectName());
                projectSlim.setToken(tblProject.getToken());
                projectSlim.setCreateTime(tblProject.getCreateTime());
                projectSlim.setUpdateTime(tblProject.getUpdateTime());
                projectSlim.setStat(new ProjectStat());
                Optional<ProjectStat> optionalProjectStat = projectList.stream().filter(f -> f.getProjectName().equalsIgnoreCase(projectSlim.getProjectName())).findAny();
                optionalProjectStat.ifPresent(projectSlim::setStat);
                projectSlimList.add(projectSlim);
            }
            responseData.setRows(projectSlimList);
            responseData.setTotal(projectPageList.getTotalElements());
            response.setData(responseData);

        } catch (Exception ex) {
            logger.error("getpagelist project error," + ex.getMessage());
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }

    /**
     * 获取指定项目的统计信息
     *
     * @param projectNameList 项目编码列表
     * @return 项目统计信息
     */
    private List<ProjectStat> getTblProjectList(List<String> projectNameList) {
        Specification<TblProjectStat> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();

            if (projectNameList.size() > 0) {
                CriteriaBuilder.In<String> in = cb.in(root.get("projectName"));
                for (String projectName : projectNameList)
                    in.value(projectName);
                predicates.add(in);
            }
            Predicate[] pre = new Predicate[predicates.size()];
            return cb.and(predicates.toArray(pre));
        };

        List<TblProjectStat> tblProjectList = this.projectStatRepository.findAll(spec);

        List<ProjectStat> projectList = new ArrayList<>();
        for (TblProjectStat tblProjectStat : tblProjectList) {
            ProjectStat projectStat = new ProjectStat();
            projectStat.setProjectName(tblProjectStat.getProjectName());
            BeanUtils.copyProperties(tblProjectStat, projectStat);
            projectList.add(projectStat);
        }

        return projectList;
    }

    @Override
    public GetProjectListResponse getProjectList() {
        GetProjectListResponse response = new GetProjectListResponse();
        try {
            List<Project> projectList = new ArrayList<Project>();
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null) {
                List<TblProject> tblProjectList = getTblProjectList();

                for (TblProject tblProject : tblProjectList) {
                    Project project = new Project();
                    project.setId(tblProject.getId());
                    project.setProjectName(tblProject.getProjectName());
                    project.setProjectDisplayName(tblProject.getProjectDisplayName());
                    project.setExcludedIp(tblProject.getExcludedIp());
                    project.setExcludedUa(tblProject.getExcludedUa());
                    project.setExcludedUrlParams(tblProject.getExcludedUrlParams());
                    project.setSearchwordKey(tblProject.getSearchwordKey());
                    project.setSearchwordCategoryKey(tblProject.getSearchwordCategoryKey());
                    project.setRootUrls(tblProject.getRootUrls());
                    projectList.add(project);
                }
            }
            response.setData(projectList);

        } catch (Exception ex) {
            logger.error("getlist project error," + ex.getMessage());
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }

    /**
     * 获取所有项目
     *
     * @return 项目信息列表
     */
    private List<TblProject> getTblProjectList() {
        Sort sort = Sort.by(Sort.Direction.ASC, "createTime");

        Specification<TblProject> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            predicates.add(cb.notEqual(root.get("status"), ProjectStatus.Deleted.getName()));
            Predicate[] pre = new Predicate[predicates.size()];
            return cb.and(predicates.toArray(pre));
        };
        List<TblProject> tblProjectList = this.projectRepository.findAll(spec, sort);
        return tblProjectList;
    }

    @Override
    public GetStatResponse getStat(StatGlobalRequest statGlobalRequest) {
        GetStatResponse response = new GetStatResponse();
        try {
            ProjectStat projectStat = new ProjectStat();
            if (statGlobalRequest.getStartDate() == null && statGlobalRequest.getEndDate() == null) {
                List<ProjectStat> projectList = getTblProjectList(new ArrayList<>(Arrays.asList(constsDataHolder.getGlobalProjectCode())));

                if (!projectList.isEmpty()) {
                    projectStat = projectList.get(0);
                }
            } else {
                List<ProjectStat> projectList = statProjectWithGlobal(getTblProjectList(), statGlobalRequest.getStartDate(), statGlobalRequest.getEndDate());
                if (!projectList.isEmpty()) {
                    projectStat = projectList.get(projectList.size() - 1);
                }
            }
            response.setData(projectStat);
        } catch (Exception ex) {
            logger.error("getStat error," + ex.getMessage());
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public void statByProjectName() {
        try {

            List<TblProject> tblProjectList = getTblProjectList();
            statLogByProjectName(tblProjectList);

            List<ProjectStat> projectList = statProjectWithGlobal(tblProjectList, null, null);

            //存库
            Specification<TblProjectStat> spec = (root, query, cb) -> {
                return null;
            };

            List<TblProjectStat> tblProjectStatList = projectStatRepository.findAll(spec);
            for (ProjectStat projectStat : projectList) {
                Optional<TblProjectStat> optionalTblProjectStat = tblProjectStatList.stream().filter(f -> f.getProjectName().equalsIgnoreCase(projectStat.getProjectName())).findAny();

                TblProjectStat tblProjectStat = optionalTblProjectStat.orElseGet(TblProjectStat::new);
                tblProjectStat.setProjectName(projectStat.getProjectName());
                tblProjectStat.setLogRecordCount(projectStat.getLogRecordCount());
                tblProjectStat.setLogSpaceSize(projectStat.getLogSpaceSize());
                tblProjectStat.setLogLatestTime(projectStat.getLogLatestTime());
                tblProjectStat.setDbRecordCount(projectStat.getDbRecordCount());
                tblProjectStat.setDbSpaceSize(projectStat.getDbSpaceSize());
                tblProjectStat.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                tblProjectStat.setLogDays(projectStat.getLogDays());
                tblProjectStat.setDbFirstTime(projectStat.getDbFirstTime());
                tblProjectStat.setDbLatestTime(projectStat.getDbLatestTime());
                projectStatRepository.save(tblProjectStat);
            }
        } catch (Exception ex) {
            this.logger.error("statByProjectName err", ex);
        }
    }

    /**
     * 全局项目和指定项目的统计
     *
     * @param tblProjectList 指定项目编码列表
     * @param startDate      统计开始日期
     * @param endDate        统计结束日期
     * @return 项目统计信息
     */
    private List<ProjectStat> statProjectWithGlobal(List<TblProject> tblProjectList, Timestamp startDate, Timestamp endDate) {

        Long globalLogLines = 0L;
        Long globalLogSize = 0L;
        Long globalLogLatestTime = 0L;
        Long globalDbFirstTime = 0L;
        Long globalDbRecordCount = 0L;
        Long globalDbLatestTime = 0L;

        ProjectStat dbSpaceSizeStat = getDbSpace(startDate, endDate);

        List<ProjectStat> projectList = statProject(tblProjectList, startDate, endDate, dbSpaceSizeStat);

        for (ProjectStat projectStat : projectList) {
            // 计算全局指标
            if (projectStat.getLogRecordCount() != null) {
                globalLogLines += projectStat.getLogRecordCount();
            }
            if (projectStat.getLogSpaceSize() != null) {
                globalLogSize += projectStat.getLogSpaceSize();
            }
            if (projectStat.getLogLatestTime() != null && projectStat.getLogLatestTime().getTime() > globalLogLatestTime) {
                globalLogLatestTime = projectStat.getLogLatestTime().getTime();
            }
            if (projectStat.getDbFirstTime() != null) {
                if (globalDbFirstTime == 0) {
                    globalDbFirstTime = projectStat.getDbFirstTime().getTime();
                } else if (projectStat.getDbFirstTime().getTime() < globalDbFirstTime) {
                    globalDbFirstTime = projectStat.getDbFirstTime().getTime();
                }
            }
            if (projectStat.getDbLatestTime() != null && projectStat.getDbLatestTime().getTime() > globalDbLatestTime) {
                globalDbLatestTime = projectStat.getDbLatestTime().getTime();
            }
            if (projectStat.getDbRecordCount() != null) {
                globalDbRecordCount += projectStat.getDbRecordCount();
            }
        }
        // 计算全局

        ProjectStat globalStat = new ProjectStat();
        globalStat.setProjectName(constsDataHolder.getGlobalProjectCode());
        globalStat.setLogSpaceSize(globalLogSize);
        globalStat.setLogRecordCount(globalLogLines);
        globalStat.setLogLatestTime(new Timestamp(globalLogLatestTime));
        if (projectList.size() == 1) {
            globalStat.setDbSpaceSize(projectList.get(0).getDbSpaceSize());
        }
        if (globalDbFirstTime > 0) {
            globalStat.setDbFirstTime(new Timestamp(globalDbFirstTime));
        }
        if (globalDbLatestTime > 0) {
            globalStat.setDbLatestTime(new Timestamp(globalDbLatestTime));
        }
        globalStat.setDbRecordCount(globalDbRecordCount);
        if (dbSpaceSizeStat != null) {
            globalStat.setDbSpaceSize(dbSpaceSizeStat.getDbSpaceSize());
        }
        projectList.add(globalStat);
        return projectList;
    }

    /**
     * 指定项目的统计
     *
     * @param tblProjectList     指定项目编码列表
     * @param startDate          统计开始日期
     * @param endDate            统计结束日期
     * @param projectDbSpaceStat 项目数据库表的统计信息
     * @return 项目统计信息
     */
    private List<ProjectStat> statProject(List<TblProject> tblProjectList, Timestamp startDate, Timestamp endDate, ProjectStat projectDbSpaceStat) {

        Integer statProjectCount = 0;
        if (startDate == null && endDate == null) {
            statProjectCount = projectStatRepository.getStatProjectCount();
        }

        List<ProjectStat> projectList = new ArrayList<>();

        List<ProjectStatInterface> projectLogStatList = projectLogStatRepository.getStatGroupByProject(startDate, endDate);

        for (TblProject tblProject : tblProjectList) {
            String projectName = tblProject.getProjectName();
            ProjectStat projectStat = new ProjectStat();
            projectStat.setProjectName(projectName);

            // 日志统计
            Optional<ProjectStatInterface> optionalProjectLogStat = projectLogStatList.stream().filter(f -> f.getProjectName().equalsIgnoreCase(projectName)).findAny();
            if (optionalProjectLogStat.isPresent()) {
                BeanUtils.copyProperties(optionalProjectLogStat.get(), projectStat);
            }
            // 只有一个项目时，才赋值表空间计数
            if (statProjectCount != null && statProjectCount == 1 && projectDbSpaceStat != null) {
                projectStat.setDbSpaceSize(projectDbSpaceStat.getDbSpaceSize());
            }
            projectList.add(projectStat);
        }
        return projectList;
    }


    /**
     * 指定项目的统计
     *
     * @param tblProjectList 指定项目列表
     */
    private void statLogByProjectName(List<TblProject> tblProjectList) {
        try {

            List<TblProjectLogStat> projectLogStatList = new ArrayList<>();
            List<ProjectMaxStatDate> projectMaxStatDateList = projectLogStatRepository.getMaxStatDateGroupByProject();
            for (TblProject tblProject : tblProjectList) {
                Timestamp day = null;
                Optional<ProjectMaxStatDate> optionalProjectMaxStatDate = projectMaxStatDateList.stream().filter(f -> f.getProjectName().equalsIgnoreCase(tblProject.getProjectName())).findAny();
                if (optionalProjectMaxStatDate.isPresent()) {
                    day = optionalProjectMaxStatDate.get().getStatDate();
                    day = new Timestamp(day.getTime() - 20000);
                    day = new Timestamp(yMhFORMAT.get().parse(yMhFORMAT.get().format(day)).getTime());
                }

                List<TblProjectLogStat> projectLogStatListOfProject = statLogByProjectNameSinceSomeDay(tblProject, day);
                List<TblProjectLogStat> projectDbStatListOfProject = statDbByProjectNameSinceSomeDay(tblProject, day);

                List<Timestamp> statDateList = projectLogStatListOfProject.stream().map(TblProjectLogStat::getStatDate).collect(Collectors.toList());
                statDateList.addAll(projectDbStatListOfProject.stream().map(TblProjectLogStat::getStatDate).collect(Collectors.toList()));
                statDateList = statDateList.stream().distinct().collect(Collectors.toList());

                for (Timestamp statDate : statDateList) {
                    TblProjectLogStat projectLogStat = new TblProjectLogStat();
                    projectLogStat.setStatDate(statDate);
                    projectLogStat.setProjectName(tblProject.getProjectName());

                    Optional<TblProjectLogStat> optProjectLogStat = projectLogStatListOfProject.stream().filter(f -> f.getStatDate().equals(statDate)).findAny();
                    if (optProjectLogStat.isPresent()) {
                        projectLogStat.setLogLatestTime(optProjectLogStat.get().getLogLatestTime());
                        projectLogStat.setLogRecordCount(optProjectLogStat.get().getLogRecordCount());
                        projectLogStat.setLogSpaceSize(optProjectLogStat.get().getLogSpaceSize());
                    }

                    Optional<TblProjectLogStat> optProjectDbStat = projectDbStatListOfProject.stream().filter(f -> f.getStatDate().equals(statDate)).findAny();
                    if (optProjectDbStat.isPresent()) {
                        projectLogStat.setDbLatestTime(optProjectDbStat.get().getDbLatestTime());
                        projectLogStat.setDbFirstTime(optProjectDbStat.get().getDbFirstTime());
                        projectLogStat.setDbRecordCount(optProjectDbStat.get().getDbRecordCount());
                    }

                    projectLogStatList.add(projectLogStat);
                }
            }
            //存库
            for (TblProjectLogStat projectLogStat : projectLogStatList) {
                Specification<TblProjectLogStat> spec = (root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<Predicate>();
                    predicates.add(cb.equal(root.get("projectName"), projectLogStat.getProjectName()));
                    predicates.add(cb.equal(root.get("statDate"), projectLogStat.getStatDate()));
                    Predicate[] pre = new Predicate[predicates.size()];
                    return cb.and(predicates.toArray(pre));
                };

                List<TblProjectLogStat> tblprojectLogStatList = projectLogStatRepository.findAll(spec);
                TblProjectLogStat tblProjectLogStat = null;
                if (tblprojectLogStatList.isEmpty()) {
                    tblProjectLogStat = new TblProjectLogStat();
                    tblProjectLogStat.setId(UUID.randomUUID().toString());
                } else {
                    tblProjectLogStat = tblprojectLogStatList.get(0);
                }
                tblProjectLogStat.setProjectName(projectLogStat.getProjectName());
                tblProjectLogStat.setStatDate(projectLogStat.getStatDate());
                tblProjectLogStat.setLogRecordCount(projectLogStat.getLogRecordCount());
                tblProjectLogStat.setLogSpaceSize(projectLogStat.getLogSpaceSize());
                tblProjectLogStat.setLogLatestTime(projectLogStat.getLogLatestTime());
                tblProjectLogStat.setDbLatestTime(projectLogStat.getDbLatestTime());
                tblProjectLogStat.setDbFirstTime(projectLogStat.getDbFirstTime());
                tblProjectLogStat.setDbRecordCount(projectLogStat.getDbRecordCount());
                tblProjectLogStat.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                projectLogStatRepository.save(tblProjectLogStat);
            }
        } catch (Exception ex) {
            this.logger.error("statLogByProjectName err", ex);
        }
    }

    /**
     * 从指定日期开始统计项目的日志情况
     *
     * @param tblProject 项目信息
     * @param day        指定日期
     * @return 项目的每日日志统计信息
     * @throws ParseException
     */
    private List<TblProjectLogStat> statLogByProjectNameSinceSomeDay(TblProject tblProject, Timestamp day) throws ParseException {

        List<TblProjectLogStat> projectLogStatList = new ArrayList<>();
        String projectName = tblProject.getProjectName();

        String logStorePath = clklogManageSetting.getLogStorePath() + File.separator + projectName;
        File directory = new File(logStorePath);
        File[] files = directory.listFiles(new ExtensionFilter("log"));

        if (files != null) {
            for (File file : files) {
                long now = System.currentTimeMillis();
                boolean needCalc = false;
                Timestamp statDate = null;

                if (file.getName().equalsIgnoreCase("store.log")) {
                    /* 统计当前的日志文件 */
                    needCalc = true;
                    statDate = new Timestamp(yMhFORMAT.get().parse(yMhFORMAT.get().format(now)).getTime());
                } else {
                    String fileDate = file.getName().substring(6, 14);
                    statDate = new Timestamp(yMhFORMAT.get().parse(fileDate).getTime());
                    if (day == null) {
                        /* 该项目从未统计过（day == null ），则统计file文件 */
                        needCalc = true;
                    } else {
                        /* 如果当前file的日期不小于day,则统计file文件 */
                        String strDay = yMhFORMAT.get().format(day);
                        if (strDay.compareTo(fileDate) <= 0) {
                            needCalc = true;
                        }
                    }
                }
                if (needCalc) {
                    TblProjectLogStat projectLogStat = new TblProjectLogStat();
                    projectLogStat.setProjectName(projectName);
                    projectLogStat.setStatDate(statDate);
                    projectLogStat.setLogLatestTime(new Timestamp(file.lastModified()));
                    projectLogStat.setLogSpaceSize(file.length());
                    projectLogStat.setLogRecordCount(getLineNumber(file));
                    projectLogStatList.add(projectLogStat);
                }
            }
        }

        return projectLogStatList;
    }

    /**
     * 从指定日期开始统计项目的数据库表情况
     *
     * @param tblProject 项目信息
     * @param day        指定日期
     * @return 项目的每日数据库表统计信息
     */
    private List<TblProjectLogStat> statDbByProjectNameSinceSomeDay(TblProject tblProject, Timestamp day) {

        List<TblProjectLogStat> projectLogStatList = new ArrayList<>();
        String projectName = tblProject.getProjectName();
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("project_name", projectName);
        if (day == null) {
            String startDate = "1970-01-01";
            if (tblProject.getCreateTime() != null) {
                /* 该项目从未统计过（day == null ），则从项目创建时间开始统计 */
                startDate = y_M_hFORMAT.get().format(tblProject.getCreateTime());
            }
            param.addValue("start_date", startDate);
        } else {
            param.addValue("start_date", y_M_hFORMAT.get().format(day));
        }
        projectLogStatList = clickHouseJdbcTemplate.query("select stat_date, count(1) as dbRecordCount,min(log_time) as dbFirstTime,max(log_time) as dbLatestTime from log_analysis where stat_date>=:start_date and  project_name=:project_name group by stat_date", param, new BeanPropertyRowMapper<TblProjectLogStat>(TblProjectLogStat.class));

        return projectLogStatList;
    }

    /**
     * 统计数据库的占用空间
     *
     * @param startDate 统计开始日期
     * @param endDate   统计结束日期
     * @return 项目统计信息
     */
    private ProjectStat getDbSpace(Timestamp startDate, Timestamp endDate) {
        ProjectStat dbSpaceSizeStat = null;
        if (startDate == null && endDate == null) {
            List<ProjectStat> projectDbSizeList = new ArrayList<>();
            try {
                projectDbSizeList = clickHouseJdbcTemplate.query("SELECT '" + constsDataHolder.getGlobalProjectCode() + "' as projectName, sum(data_uncompressed_bytes) AS dbSpaceSize FROM system.parts WHERE active  AND table = 'log_analysis'", new MapSqlParameterSource(), new BeanPropertyRowMapper<ProjectStat>(ProjectStat.class));
                dbSpaceSizeStat = projectDbSizeList.get(0);
            } catch (Exception ex) {
                this.logger.error("getDbSpace err", ex);
            }
        }
        return dbSpaceSizeStat;
    }

    /**
     * 计算文件行数
     *
     * @param file 文件路径
     * @return long 行数
     */
    private long getLineNumber(File file) {
        if (file.exists()) {
            try {
                FileReader fileReader = new FileReader(file);
                LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
                lineNumberReader.skip(Long.MAX_VALUE);
                long lines = lineNumberReader.getLineNumber() + 1;
                fileReader.close();
                lineNumberReader.close();
                return lines;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    @Override
    public void reloadProjectSetting() {
        try {
            HashMap<String, ProjectSetting> htProjectSetting = new HashMap<>();
            List<TblProject> tblProjectList = getTblProjectList();
            for (TblProject project : tblProjectList) {
                ProjectSetting projectSetting = new ProjectSetting();
                BeanUtils.copyProperties(project, projectSetting);
                htProjectSetting.put(project.getProjectName(), projectSetting);
            }

            GetSettingResponse getSettingResponse = getSetting();
            if (getSettingResponse.getCode() == 200) {
                ProjectSetting globalSetting = new ProjectSetting();
                BeanUtils.copyProperties(getSettingResponse.getData(), globalSetting);
                htProjectSetting.put(constsDataHolder.getGlobalProjectCode(), globalSetting);
            }

            String projectSettingContent = objectMapper.writeValueAsString(htProjectSetting);
            redisTemplate.opsForValue().set(redisConstsConfig.getProjectSettingKey(), projectSettingContent);
        } catch (Exception ex) {
            logger.error("reloadProjectSetting err ", ex);
        }
    }

    @Override
    public void cacheCityEngChsMap() {
        List<String> cityEngChsList = IOUtil.readAllLines(System.getProperty("user.dir") + File.separator + "setting" + File.separator
                + "city-eng-chs-map.txt");

        HashMap<String, String> htCityMap = new HashMap<>();
        for (String line : cityEngChsList) {
            String[] pair = line.split(";");
            String country = pair[0].toLowerCase(Locale.ROOT);
            String province = pair[1];
            String[] provinceArr = province.split("-", -1);
            String key = country + "_" + provinceArr[0].toLowerCase(Locale.ROOT);
            htCityMap.put(key, provinceArr[1]);
            for (int i = 1; i < pair.length; i++) {
                String district = pair[i];
                String[] districtArr = district.split("-", -1);
                String[] cities = districtArr[0].split(",", -1);
                for (String city : cities) {
                    htCityMap.put(key + "_" + city.toLowerCase(Locale.ROOT), districtArr[1]);
                }
            }
        }

        RedisSerializer<String> rs = (RedisSerializer<String>) redisTemplate.getKeySerializer();
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] byKey = rs.serialize(redisConstsConfig.getCityEngChsMapKey());
                for (Map.Entry<String, String> entry : htCityMap.entrySet()) {
                    connection.hSet(byKey, rs.serialize(entry.getKey()), rs.serialize(entry.getValue()));
                }
                return null;
            }
        });
    }

    @Override
    public void cacheCountryEngChsMap() {
        List<String> regionEngChsList = IOUtil.readAllLines(System.getProperty("user.dir") + File.separator + "setting" + File.separator
                + "country-eng-chs-map.txt");
        if (!regionEngChsList.isEmpty()) {
            RedisSerializer<String> rs = (RedisSerializer<String>) redisTemplate.getKeySerializer();

            redisTemplate.executePipelined(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    byte[] byKey = rs.serialize(redisConstsConfig.getCityEngChsMapKey());
                    for (String line : regionEngChsList) {
                        String[] pair = line.split("-");
                        if (pair.length >= 2) {
                            connection.hSet(byKey, rs.serialize(pair[0].toLowerCase(Locale.ROOT)), rs.serialize(pair[1]));
                        }
                    }
                    return null;
                }
            });
        }
    }

}
