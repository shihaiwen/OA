package com.whir.rd.common;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lihui
 * @date 2017��4��17��
 * @desc ϵͳ���õĹ��÷���
 */
public class OaUtil {
	ProJdbcUtils db = new ProJdbcUtils();

	/**
	 * ȡ���ڶ����֯�͵�����ɫ��Χ�ڵ��û���Ϣ
	 * 
	 * @param orgIds
	 *            ��֯ID
	 * @param roleId
	 *            ��ɫID
	 * @return �ڶ����֯�͵�����ɫ��Χ�ڵ��û���ֵ�ԣ�key:userId value:userName
	 * @throws SQLException
	 */
	public List<Map<String, Object>> findRoleUsers(String[] orgIds,
			String roleId) throws SQLException {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		// ���߽�ɫidȡ�ý�ɫ�û�
		String sql = "select roleUserId from org_role where role_Id=" + roleId;
		Map<String, Object> map = db.findSimpleResult(sql, null);
		String roleUserId = map.get("ROLEUSERID") == null ? "" : map.get(
				"ROLEUSERID").toString();
		if (!roleUserId.equals("")) {
			roleUserId = roleUserId.substring(1, roleUserId.length() - 1);
			String ids = "";
			String[] roleUserIdArr = roleUserId.split("\\$\\$");
			for (int i = 0; i < roleUserIdArr.length; i++) {
				ids += roleUserIdArr[i] + ",";
			}
			System.out.println("ids===" + ids);
			if (ids.endsWith(",")) {
				ids = ids.substring(0, ids.length() - 1);
				if (orgIds != null && orgIds.length > 0) {
					for (String orgId : orgIds) {
						List<Map<String, Object>> temp = this
								.findUsersByOrgIdAndUserIds(ids, orgId);
						if (temp != null) {
							resultList.addAll(temp);
						}
					}
				}
			}
		}
		return resultList;
	}

	/**
	 * ������֯Id���û�Idȡ���û���Ϣ
	 * 
	 * @param userIds
	 * @param orgId
	 * @return
	 */
	public List<Map<String, Object>> findUsersByOrgIdAndUserIds(String userIds,
			String orgId) {
		List<Map<String, Object>> orgEmpIdList = null;
		try {
			String sql = "select emp.emp_id,emp.empName,emp.useraccounts,emp.EMPMOBILEPHONE,emp.EMPBUSINESSPHONE "
					+ "from  org_employee emp left join org_organization_user ou "
					+ "on emp.emp_id =ou.emp_id left join  org_organization org on "
					+ "ou.org_id = org.org_id where org.orgidstring"
					+ " like '%$"
					+ orgId
					+ "$%' and ou.emp_id in("
					+ userIds
					+ ")";
			orgEmpIdList = db.findMoreResult(sql, null);
		} catch (Exception e) {
			return null;
		}
		return orgEmpIdList;
	}

	/**
	 * ����GroupId��ȡ���û���Ϣ @618337@@947575@
	 * 
	 * @return
	 * @throws SQLException 
	 */
	public Set<String> findUserIdsByGroupIds(String groupIds) throws SQLException {
		if(groupIds.contains("@")){
			groupIds = groupIds.substring(1,groupIds.length()-1);
		} else {
			return null;
		}
		groupIds = groupIds.replaceAll("@@",",");
System.out.println("groupIds===="+groupIds);
System.out.println("sql===="+"select groupuserstring from org_group where group_id in ("+groupIds+")");
		List<Object> userList = db.findClobList("select groupuserstring from org_group where group_id in ("+groupIds+")");
		Set<String> userIdSet = new HashSet<String>();
		for(Object userIds : userList){
			if(userIds != null){
				String[] userIdArr = this.formatUserId(userIds.toString());
				for(String userId : userIdArr){
					userIdSet.add(userId);
				}
			}
		}
		return  userIdSet;
	}
	
	public String[] formatUserId(String userIds){
		if(userIds.contains("$")){
			userIds = userIds.substring(1,userIds.length()-1);
		}else {
			return null;
		}
		return userIds.split("\\$\\$");
	}
	
	public static void main(String[] args) {
		String userIds = "$618337$$947575$";
		if(userIds.contains("$")){
			System.out.println("===========");
			userIds = userIds.substring(1,userIds.length()-1);
		}
		for(String s : userIds.split("\\$\\$")){
			System.out.println(s);
		}
	}
	
	/**
	 * ������֯Id���û�Idȡ���û���Ϣ
	 * 
	 * @param userIds
	 * @param orgId
	 * @return
	 */
	public String findUserAccountsById(long empId) {
		try {
			String sql = "select emp.useraccounts from org_employee emp where emp_id = "+empId;
			List<Object> list = db.findList(sql);
			if(list != null && list.size() > 0){
				return list.get(0).toString();
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
}
