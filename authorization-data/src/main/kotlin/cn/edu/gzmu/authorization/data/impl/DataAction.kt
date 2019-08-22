package cn.edu.gzmu.authorization.data.impl

import cn.edu.gzmu.authorization.common.IS_ENABLE_TRUE

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/22 下午1:16
 */
const val SERVICE_NAME = "data-storage-eb-service"
const val SERVICE_ADDRESS = "service.data.storage"

const val API_DATA = "/"

const val RETRIEVE = """
  SELECT * FROM sys_data WHERE parent_id = ? and $IS_ENABLE_TRUE;  
"""
