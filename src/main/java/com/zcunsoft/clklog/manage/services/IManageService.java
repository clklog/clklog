package com.zcunsoft.clklog.manage.services;

import com.zcunsoft.clklog.manage.models.global.*;
import com.zcunsoft.clklog.manage.models.project.*;

/**
 * 管理服务接口.
 */
public interface IManageService {

    /**
     * 保存全局项目配置.
     *
     * @param saveGlobalSettingRequest 全局项目配置
     * @return 是否成功保存
     */
    SaveSettingResponse saveSetting(SaveGlobalSettingRequest saveGlobalSettingRequest);

    /**
     * 获取全局项目配置.
     *
     * @return 全局项目配置
     */
    GetSettingResponse getSetting();

    /**
     * 获取项目配置.
     *
     * @param getProjectRequest 获取项目配置的请求
     * @return 项目配置
     */
    GetProjectResponse getProject(GetProjectRequest getProjectRequest);

    /**
     * 添加项目.
     *
     * @param addProjectRequest 项目信息
     * @return 添加项目的响应结果
     */
    AddProjectResponse addProject(AddProjectRequest addProjectRequest);

    /**
     * 编辑项目.
     *
     * @param editProjectRequest 项目信息
     */
    void editProject(EditProjectRequest editProjectRequest);

    /**
     * 删除项目.
     *
     * @param delProjectRequest 删除项目的请求
     */
    void deleteProject(DeleteProjectRequest delProjectRequest);

    /**
     * 分页获取项目列表.
     *
     * @param queryProjectRequest 分页获取项目列表的请求
     * @return 项目分页信息
     */
    QueryProjectResponse getProjectPageList(QueryProjectRequest queryProjectRequest);

    /**
     * 获取所有项目
     *
     * @return 项目列表
     */
    GetProjectListResponse getProjectList();

    /**
     * 获取项目统计信息.
     *
     * @param statGlobalRequest 获取项目统计信息的请求
     * @return 统计信息
     */
    GetStatResponse getStat(StatGlobalRequest statGlobalRequest);

    /**
     * 统计项目.
     */
    void statByProjectName();

    /**
     * 缓存项目配置.
     */
    void reloadProjectSetting();

}
