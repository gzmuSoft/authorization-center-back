package cn.edu.gzmu.authorization.res.impl

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/21 下午7:29
 */
const val SERVICE_NAME = "res-storage-eb-service"
const val SERVICE_ADDRESS = "service.res.storage"

const val API_RES = "/"
const val API_RES_IS_ENABLE = "/enable"

const val ENABLE_RES = """
  UPDATE sys_res
  SET is_enable = ?
  WHERE id = ?
"""
const val RETRIEVE = """
  SELECT r.*, sr.id role_id, sr.name role_name
  FROM sys_res r
           INNER JOIN sys_role_res srr ON r.id = srr.res_id
           INNER JOIN sys_role sr ON srr.role_id = sr.id
  WHERE r.is_enable = 1
    AND sr.is_enable = 1
    AND srr.is_enable = 1
  ORDER BY r.sort, r.id;
"""
