package cn.edu.gzmu.authorization.role.impl

import cn.edu.gzmu.authorization.common.IS_ENABLE_TRUE

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/21 下午2:18
 */
const val SERVICE_NAME = "role-storage-eb-service"
const val SERVICE_ADDRESS = "service.role.storage"

const val API_ROLE = "/"
const val API_ROLE_IS_ENABLE = "/enable"

const val RETRIEVE = "SELECT * FROM sys_role"
const val INSERT_ROLE = """
  INSERT INTO sys_role (name, spell, des, icon_cls, parent_id, sort, create_user, create_time, 
                        modify_user, modify_time, remark, is_enable)
  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
"""
const val UPDATE_ROLE = """
  UPDATE sys_role
  SET name        = ?,
      spell       = ?,
      des         = ?,
      icon_cls    = ?,
      parent_id   = ?,
      sort        = ?,
      modify_user = ?,
      modify_time = ?,
      remark      = ?,
      is_enable   = ?
  WHERE id = ? AND $IS_ENABLE_TRUE
"""
const val ENABLE_ROLE = """
  UPDATE sys_role
  SET is_enable = ?
  WHERE id = ?
"""
