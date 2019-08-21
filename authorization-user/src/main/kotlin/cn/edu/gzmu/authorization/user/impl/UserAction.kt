package cn.edu.gzmu.authorization.user.impl

import cn.edu.gzmu.authorization.common.IS_ENABLE_TRUE
import cn.edu.gzmu.authorization.common.ORDER_BY_SORT

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/17 下午5:28
 */
const val SERVICE_NAME = "user-storage-eb-service"
const val SERVICE_ADDRESS = "service.user.storage"

const val API_USER = "/"
const val API_USER_ONE = "/:id"
const val API_EXIST = "/exist"
const val API_STATUS = "/status"

const val RETRIEVE_PAGE = """
  SELECT u.*, sr.id role_id, sr.name role_name
  FROM sys_user u
           INNER JOIN sys_user_role sur ON u.id = sur.user_id
           INNER JOIN sys_role sr ON sur.role_id = sr.id
  WHERE u.is_enable = 1
    AND sr.is_enable = 1
    AND sur.is_enable = 1
    AND (u.name LIKE ? OR u.email LIKE ? OR u.phone LIKE ?)
  ORDER BY u.sort, u.id
  LIMIT ?,?;
"""
const val RETRIEVE_USER = "SELECT * FROM sys_user WHERE id = ? and $IS_ENABLE_TRUE $ORDER_BY_SORT"
const val INSERT_USER = """
      INSERT INTO sys_user 
      (name, spell, pwd, status, icon, email, phone, online_status, sort, create_user,
                            modify_user, remark, is_enable)
      values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
"""
const val INSERT_USER_ROLE = """
  INSERT INTO sys_user_role (user_id, role_id, is_enable)
  VALUES (?, ?, ?)
"""
const val RETRIEVE_USER_ROLE = """
  SELECT * FROM sys_user_role WHERE user_id = ?  
"""
const val UPDATE_USER_ROLE = """
  UPDATE sys_user_role SET is_enable = 1 WHERE id = ?
"""
const val DELETE_USER_ROLE = """
  UPDATE sys_user_role SET is_enable = 0 WHERE id = ?
"""
const val UPDATE_USER = """
      UPDATE sys_user SET name=?, spell=?, pwd=?, status=?, icon=?, email=?, phone=?,
       online_status=?, sort=?,modify_user=?, remark=?, is_enable=?
       WHERE id = ? AND $IS_ENABLE_TRUE
"""
const val EXIST_USER = """
      SELECT * FROM sys_user WHERE $IS_ENABLE_TRUE
"""
const val STATUS_CHANGE = """
      UPDATE sys_user
      SET status = ?
      WHERE id = ?
"""
