package cn.edu.gzmu.authorization.user.impl

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/17 下午5:28
 */
const val RETRIEVE_USER = "SELECT * FROM sys_user WHERE id = ?"
const val SERVICE_NAME = "user-storage-eb-service"
const val SERVICE_ADDRESS = "service.user.storage"

const val API_RETRIEVE_ONE = "/:id"
