package cn.edu.gzmu.authorization.user.impl

import cn.edu.gzmu.authorization.common.IS_ENABLE_TRUE

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/17 下午5:28
 */
const val RETRIEVE_PAGE =
  "SELECT * FROM sys_user WHERE (name like ? or email like ? or phone like ?) and $IS_ENABLE_TRUE limit ?,?"
const val RETRIEVE_USER = "SELECT * FROM sys_user WHERE id = ? and $IS_ENABLE_TRUE"
const val INSERT_USER = """
      INSERT INTO sys_user 
      (name, spell, pwd, status, icon, email, phone, online_status, sort, create_user,
                            modify_user, remark, is_enable)
      values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
"""
const val UPDATE_USER = """
      UPDATE sys_user SET name=?, spell=?, pwd=?, status=?, icon=?, email=?, phone=?,
       online_status=?, sort=?, create_user=?, modify_user=?, remark=?, is_enable=?
       WHERE id = ?
"""
const val EXIST_USER = """
      SELECT * FROM sys_user WHERE $IS_ENABLE_TRUE
"""
const val SERVICE_NAME = "user-storage-eb-service"
const val SERVICE_ADDRESS = "service.user.storage"

const val API_USER = "/"
const val API_USER_ONE = "/:id"
const val API_EXIST = "/exist"
