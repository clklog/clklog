import request from '@/utils/requestManage' 

export function getAppListPage(params) {
    return request({
        url: '/app/getlistpage',
        method: 'post',
        data: params
    })
}

export function addApp(params) {
    return request({
        url: '/app/add',
        method: 'post',
        data: params
    })
}


export function editApp(params) {
    return request({
        url: '/app/edit',
        method: 'post',
        data: params
    })
}


export function deleteApp(params) {
    return request({
        url: '/app/delete',
        method: 'post',
        data: params
    })
} 
