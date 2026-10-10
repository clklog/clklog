package com.zcunsoft.clklog.manage.controllers;

import com.zcunsoft.clklog.manage.models.R;
import com.zcunsoft.clklog.manage.models.project.*;
import com.zcunsoft.clklog.manage.services.IManageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 项目管理
 */
@RestController
@RequestMapping(path = "project")
@Tag(name = "项目管理", description = "项目管理")
public class ProjectController {

    @Resource
    IManageService manageService;

    @Operation(summary = "获取项目详情")
    @RequestMapping(path = "/get", method = RequestMethod.POST)
    public GetProjectResponse get(@RequestBody GetProjectRequest getProjectRequest) {
        return manageService.getProject(getProjectRequest);
    }

    @Operation(summary = "添加项目")
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public AddProjectResponse add(@RequestBody AddProjectRequest addProjectRequest) {
        return manageService.addProject(addProjectRequest);
    }

    @SuppressWarnings("rawtypes")
    @Operation(summary = "编辑项目")
    @RequestMapping(path = "/edit", method = RequestMethod.POST)
    public R edit(@RequestBody EditProjectRequest editProjectRequest) {
        manageService.editProject(editProjectRequest);
        return R.ok();
    }

    @SuppressWarnings("rawtypes")
    @Operation(summary = "删除项目")
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public R delete(@RequestBody DeleteProjectRequest delProjectRequest) {
        manageService.deleteProject(delProjectRequest);
        return R.ok();
    }

    @Operation(summary = "获取项目列表")
    @RequestMapping(path = "/getlist", method = RequestMethod.POST)
    public QueryProjectResponse getlist(@RequestBody QueryProjectRequest queryProjectRequest) {
        return manageService.getProjectPageList(queryProjectRequest);
    }

    @Operation(summary = "获取授权项目")
    @RequestMapping(path = "/getMyList", method = RequestMethod.POST)
    public GetProjectListResponse getMyList() {
        return manageService.getProjectList();
    }
}
