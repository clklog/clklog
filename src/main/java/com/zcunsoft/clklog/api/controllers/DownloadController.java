package com.zcunsoft.clklog.api.controllers;

import com.zcunsoft.clklog.api.models.channel.GetChannelDetailResponse;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailResponse;
import com.zcunsoft.clklog.api.models.searchword.GetSearchWordDetailResponse;
import com.zcunsoft.clklog.api.models.sourcewebsite.GetSourceWebsiteDetailPageResponse;
import com.zcunsoft.clklog.api.models.trend.FlowDetail;
import com.zcunsoft.clklog.api.models.trend.GetFlowTrendDetailResponse;
import com.zcunsoft.clklog.api.models.uservisit.BaseUserVisit;
import com.zcunsoft.clklog.api.models.visitor.GetVisitorDetailResponse;
import com.zcunsoft.clklog.api.poi.DownloadRequest;
import com.zcunsoft.clklog.api.poi.ExcelExportUtils;
import com.zcunsoft.clklog.api.services.IExportReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(path = "download")
@Tag(name = "下载", description = "下载")
public class DownloadController {

    @Resource
    IExportReportService exportReportService;
    
    @Operation(summary = "下载流量趋势分析")
    @RequestMapping(path = "/exportFlowTrendDetail", method = RequestMethod.POST)
    public void exportFlowTrendDetail(@RequestBody DownloadRequest downloadRequest, HttpServletRequest request,HttpServletResponse respones) {
    	GetFlowTrendDetailResponse getFlowTrendDetailResponse = exportReportService.getFlowTrendDetail(downloadRequest);
    	try {
    		ExcelExportUtils.exportFlowTrendDetail(getFlowTrendDetailResponse.getData().getDetail(),getFlowTrendDetailResponse.getData().getTotal(),downloadRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载搜索词分析")
    @RequestMapping(path = "/exportSearchWordDetail", method = RequestMethod.POST)
    public void exportSearchWordDetail(@RequestBody DownloadRequest downloadRequest, HttpServletRequest request,HttpServletResponse respones) {
    	GetSearchWordDetailResponse getSearchWordDetailResponse = exportReportService.getSearchWordDetail(downloadRequest);
    	try {
    		ExcelExportUtils.exportSearchWordDetail(getSearchWordDetailResponse.getData().getRows(),downloadRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载地域分析")
    @RequestMapping(path = "/exportAreaDetail", method = RequestMethod.POST)
    public void exportAreaDetail(@RequestBody DownloadRequest downloadRequest, HttpServletRequest request,HttpServletResponse respones) {
    	List<FlowDetail> areaDetailList = exportReportService.getAreaDetailList(downloadRequest);
    	FlowDetail totalAreaDetail = exportReportService.getAreaDetailTotal(downloadRequest);
    	try {
    		ExcelExportUtils.exportAreaDetail(areaDetailList,totalAreaDetail,downloadRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载受访页面分析")
    @RequestMapping(path = "/exportVisitUriDetail", method = RequestMethod.POST)
    public void exportVisitUriDetail(@RequestBody DownloadRequest downloadRequest, HttpServletRequest request,HttpServletResponse respones) {
    	List<FlowDetail> dataList = exportReportService.getVisitUriDetailList(downloadRequest);
    	FlowDetail totalData = exportReportService.getVisitUriTotal(downloadRequest);
    	try {
    		ExcelExportUtils.exportVisitUriDetail(dataList,totalData,downloadRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载来源网站分析")
    @RequestMapping(path = "/exportSourceWebsiteDetail", method = RequestMethod.POST)
    public void exportSourceWebSiteDetail(@RequestBody DownloadRequest downloadRequest, HttpServletRequest request,HttpServletResponse respones) {
    	GetSourceWebsiteDetailPageResponse getSourceWebsiteDetailPageResponse = exportReportService.getSourceWebSiteDetail(downloadRequest);
    	try {
    		ExcelExportUtils.exportSourceWebSiteDetail(getSourceWebsiteDetailPageResponse.getData().getRows(),getSourceWebsiteDetailPageResponse.getData().getSummary(),downloadRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载新老访客分析")
    @RequestMapping(path = "/exportVisitorDetail", method = RequestMethod.POST)
    public void exportVisitorDetail(@RequestBody DownloadRequest downloadRequest, HttpServletRequest request,HttpServletResponse respones) {
    	GetVisitorDetailResponse getVisitorDetailResponse = exportReportService.getVisitorDetail(downloadRequest);
    	FlowDetail totalData = exportReportService.getVisitorTotal(downloadRequest);
    	try {
    		ExcelExportUtils.exportVisitorDetail(getVisitorDetailResponse.getData(),totalData,downloadRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载设备分析")
    @RequestMapping(path = "/exportDeviceDetail", method = RequestMethod.POST)
    public void exportDeviceDetail(@RequestBody DownloadRequest downloadRequest, HttpServletRequest request,HttpServletResponse respones) {
    	GetDeviceDetailResponse getDeviceDetailResponse = exportReportService.getDeviceDetail(downloadRequest);
    	try {
    		ExcelExportUtils.exportDeviceDetail(getDeviceDetailResponse.getData(),downloadRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载渠道分析")
    @RequestMapping(path = "/exportChannelDetail", method = RequestMethod.POST)
    public void exportChannelDetail(@RequestBody DownloadRequest downloadRequest, HttpServletRequest request,HttpServletResponse respones) {
    	GetChannelDetailResponse getChannelDetailResponse = exportReportService.getChannelDetail(downloadRequest);
    	try {
    		ExcelExportUtils.exportChannelDetail(getChannelDetailResponse.getData(),downloadRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载用户行为分析")
    @RequestMapping(path = "/exportVisitorList", method = RequestMethod.POST)
    public void exportVisitorList(@RequestBody DownloadRequest downloadRequest, HttpServletRequest request,HttpServletResponse respones) {
    	List<FlowDetail> detailList =  exportReportService.getVisitorList(downloadRequest);
    	FlowDetail total = exportReportService.getVisitorTotal(downloadRequest);
    	try {
    		ExcelExportUtils.exportVisitorList(detailList,total, downloadRequest.getCols(),request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    @Operation(summary = "下载用户忠诚度分析")
    @RequestMapping(path = "/exportVisitor", method = RequestMethod.POST)
    public void exportVisitor(@RequestBody DownloadRequest downloadRequest, HttpServletRequest request,HttpServletResponse respones) {
    	List<BaseUserVisit> pvList =  exportReportService.getUserPv(downloadRequest);
    	List<BaseUserVisit> visitList =  exportReportService.getUserVisit(downloadRequest);
    	List<BaseUserVisit> visitTimeList =  exportReportService.getUserVisitTime(downloadRequest);
    	try {
    		ExcelExportUtils.exportVisitorList(pvList,visitList,visitTimeList,request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
