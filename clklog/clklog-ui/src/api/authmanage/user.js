// import request from '@/utils/requestAuthManage'
import request from '@/utils/requestManage'
export function getUserListPage(params) {
    return request({
        url: '/user/getlistpage',
        method: 'post',
        data: params
    })
}

export function deleteUser(params) {
    return request({
        url: '/user/delete',
        method: 'post',
        data: params
    })
}


export function editUser(params) {
    return request({
        url: '/user/edit',
        method: 'post',
        data: params
    })
}


export function addUser(params) {
    return request({
        url: '/user/add',
        method: 'post',
        data: params
    })
}


export function getUser(params) {
    return request({
        url: '/user/get',
        method: 'post',
        data: params
    })
}

export function modifyPassword(params) {
    return request({
        url: '/user/modifyPassword',
        method: 'post',
        data: params
    })
}

