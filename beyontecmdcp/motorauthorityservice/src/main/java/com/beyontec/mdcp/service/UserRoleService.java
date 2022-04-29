package com.beyontec.mdcp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.beyontec.mdcp.dto.AccessRightsInfo;
import com.beyontec.mdcp.dto.LabelAccessRightsInfo;
import com.beyontec.mdcp.dto.MasterRolesDTO;
import com.beyontec.mdcp.dto.MasterRolesListDTO;
import com.beyontec.mdcp.dto.RolesInfo;
import com.beyontec.mdcp.dto.RolesInfoDto;
import com.beyontec.mdcp.dto.RolesModuleDto;
import com.beyontec.mdcp.dto.UserRolesDto;
import com.beyontec.mdcp.exception.BadDataExceptionHandler;
import com.beyontec.mdcp.model.AccessRightsMap;
import com.beyontec.mdcp.model.Roles;
import com.beyontec.mdcp.model.RolesInfos;
import com.beyontec.mdcp.model.RolesMaster;
import com.beyontec.mdcp.model.RolesModule;
import com.beyontec.mdcp.model.RolesModulesParent;
import com.beyontec.mdcp.repo.RolesMasterRepo;
import com.beyontec.mdcp.repo.RolesModulesParentRepo;
import com.beyontec.mdcp.repo.RolesModulesRepo;
import com.beyontec.mdcp.repo.RolesRepo;
import com.beyontec.mdcp.response.Response;

@Service
public class UserRoleService {

	@Autowired
	private RolesRepo rolesRepo;

	@Autowired
	private RolesModulesParentRepo rolesModulesParentRepo;

	@Autowired
	private RolesModulesRepo rolesModulesRepo;

	@Autowired
	private RolesMasterRepo rolesMasterRepo;

	public Response<Map<String, List<RolesModuleDto>>> getUserModules(String portal) {

		Response<Map<String, List<RolesModuleDto>>> response = new Response<>();

		Map<String, List<RolesModuleDto>> modulesList = new HashMap<>();
		List<RolesModulesParent> parentModules = rolesModulesParentRepo.findAll();
		for (RolesModulesParent rolesModulesParent : parentModules) {

			List<RolesModule> rolesModules = rolesModulesRepo.findByPortalAndModuleParent(portal, rolesModulesParent);
			List<RolesModuleDto> rolesModulesList = new ArrayList<>();

			for (RolesModule rolesModule : rolesModules) {
				RolesModuleDto rolesModuleDto = new RolesModuleDto();
				rolesModuleDto.setModuleLabel(rolesModule.getModuleLabel());
				rolesModuleDto.setModuleId(rolesModule.getModuleId());

				rolesModulesList.add(rolesModuleDto);
			}

			modulesList.put(rolesModulesParent.getModuleParent(), rolesModulesList);
		}

		response.setData(modulesList);
		response.setStatus(200);
		return response;
	}

	public Response<String> addOrEditRoles(UserRolesDto userRolesDto) {

		Response<String> response = new Response<>();

		RolesMaster rolesMaster = rolesMasterRepo.findByRoleId(userRolesDto.getRoleId());

		if (ObjectUtils.isEmpty(rolesMaster)) {

			RolesMaster newRolesMaster = new RolesMaster();

			int masterId = rolesMasterRepo.findAll().size() + 1;
			newRolesMaster.setMasterId(masterId);
			newRolesMaster.setRole(userRolesDto.getRoleName());
			newRolesMaster.setRoleId(userRolesDto.getRoleId());
			newRolesMaster.setUserPortal(userRolesDto.getUserPortal());			
			newRolesMaster.setCompanyId(userRolesDto.getCompanyId());
			rolesMasterRepo.save(newRolesMaster);
			Map<String, List<AccessRightsInfo>> accessRightsInfoMap = userRolesDto.getAccessRightsInfoMap();

				for (String label : accessRightsInfoMap.keySet()) {

						for (AccessRightsInfo accessRightsInfo : accessRightsInfoMap.get(label)) {

							saveUserRoles(userRolesDto, accessRightsInfo, masterId);

					}
				}
				response.setMessage("User role has been Successfully added");
		} else {

			List<Roles> existingRole = rolesRepo.findByRolesMaster(rolesMaster);

			Map<String, List<AccessRightsInfo>> accessRightsInfoMap = userRolesDto.getAccessRightsInfoMap();
			for (String label : accessRightsInfoMap.keySet()) {
				for (AccessRightsInfo accessRightsInfo : accessRightsInfoMap.get(label)) {
					if (!existingRole.contains(rolesRepo.findByRolesMasterAndRolesModule(rolesMaster,
							rolesModulesRepo.findByModuleId(accessRightsInfo.getModuleId())))) {
						saveUserRoles(userRolesDto, accessRightsInfo, rolesMaster.getMasterId());
					}
				}
			}

			for (Roles role : existingRole) {
				for (String label : accessRightsInfoMap.keySet()) {

					for (AccessRightsInfo accessRightsInfo : accessRightsInfoMap.get(label)) {
						if (accessRightsInfo.getModuleId() == role.getRolesModule().getModuleId()) {

							role.setCanAccess(accessRightsInfo.getCanAccess());
							rolesRepo.save(role);
						}

					}
				}

			}

			response.setMessage("User role has been Successfully edited");
		}
		response.setStatus(200);
		return response;

	}

	private void saveUserRoles(UserRolesDto userRolesDto, AccessRightsInfo accessRightsInfo, int masterId) {

		Roles roles = new Roles();
		roles.setRolesMaster(rolesMasterRepo.findByMasterId(masterId));

		RolesModule rolesModule = rolesModulesRepo.findByModuleId(accessRightsInfo.getModuleId());
		if (ObjectUtils.isEmpty(rolesModule)) {
			throw new BadDataExceptionHandler("Invalid roles module id");
		}
		
		roles.setRolesModule(rolesModule);
		roles.setCanAccess(accessRightsInfo.getCanAccess());

		rolesRepo.save(roles);
	}

	public Response<RolesInfoDto> getUserRoles(Integer pageSize, Integer currentPage, String userPortal, String companyid) {
		Response<RolesInfoDto> response = new Response<>();

		RolesInfoDto rolesInfo = new RolesInfoDto();
		Pageable pagable = PageRequest.of(currentPage, pageSize);

		Page<RolesMaster> rolesMasterList = null;
		if ("IC".equalsIgnoreCase(userPortal) && StringUtils.isNotBlank(companyid)) {
			rolesMasterList = rolesMasterRepo.findByCompanyId(companyid, pagable);
		} else {
			rolesMasterList = rolesMasterRepo.findByUserPortal(userPortal, pagable);
		}

		List<RolesInfo> rolesInfoList = new ArrayList<>();
		for (RolesMaster rolesMaster : rolesMasterList) {
			Response<RolesInfo> rolesinfo = getRoleInfo(rolesMaster.getRoleId(), userPortal);
			rolesInfoList.add(rolesinfo.getData());
		}

		RolesInfos rolesDetails = new RolesInfos();
		rolesDetails.setRolesInfos(rolesInfoList);
		rolesInfo.setRolesInfo(rolesDetails);
		
		if ("IC".equalsIgnoreCase(userPortal) && StringUtils.isNotBlank(companyid)) {
			rolesInfo.setTotalCount(rolesMasterRepo.findByCompanyId(companyid).size());
		} else {
			rolesInfo.setTotalCount(rolesMasterRepo.findByUserportal(userPortal).size());
		}

		response.setData(rolesInfo);
		response.setStatus(HttpStatus.OK.value());
		return response;
	}

	public Response<RolesInfo> getRoleInfo(String roleId, String userPortal) {

		Response<RolesInfo> response = new Response<>();
		RolesInfo rolesInfo = new RolesInfo();
		Map<String, List<LabelAccessRightsInfo>> accessRightsMap = new HashMap<String, List<LabelAccessRightsInfo>>();

		List<RolesModulesParent> parentModules = rolesModulesParentRepo.findAll();

		for (RolesModulesParent rolesModulesParent : parentModules) {
			List<RolesModule> rolesModules = rolesModulesRepo.findByPortalAndModuleParent(userPortal,
					rolesModulesParentRepo.findByModuleParent(rolesModulesParent.getModuleParent()));
			List<LabelAccessRightsInfo> accessRightsInfo = new ArrayList<>();
			for (RolesModule roleModule : rolesModules) {

				List<Roles> roles = rolesRepo.findByRolesModuleAndRolesMaster(roleModule,
						rolesMasterRepo.findByRoleId(roleId));
				
				for (Roles role : roles) {
					RolesMaster rolesMaster = roles.get(0).getRolesMaster();
					rolesInfo.setRoleName(rolesMaster.getRole());
					LabelAccessRightsInfo labelAccessRightsInfo = new LabelAccessRightsInfo();
					labelAccessRightsInfo.setModuleLabel(role.getRolesModule().getModuleLabel());
					labelAccessRightsInfo.setCanAccess(role.isCanAccess());
					labelAccessRightsInfo.setModuleId(role.getRolesModule().getModuleId());
					accessRightsInfo.add(labelAccessRightsInfo);
				}
			}
			if ("Platform access".equalsIgnoreCase(rolesModulesParent.getModuleParent())) {
				boolean isModuleAdded = false;
				for (RolesModule rolesModule : rolesModules) {
					isModuleAdded = false;
					for (LabelAccessRightsInfo rightsInfo : accessRightsInfo) {
						if (rightsInfo.getModuleLabel().equalsIgnoreCase(rolesModule.getModuleLabel())) {
							isModuleAdded = true;
							break;
						}
					}
					if (!isModuleAdded) {
						LabelAccessRightsInfo labelAccessRightsInfo = new LabelAccessRightsInfo();
						labelAccessRightsInfo.setModuleLabel(rolesModule.getModuleLabel());
						labelAccessRightsInfo.setCanAccess(false);
						labelAccessRightsInfo.setModuleId(rolesModule.getModuleId());
						accessRightsInfo.add(labelAccessRightsInfo);
					}
				}
			}
			accessRightsMap.put(rolesModulesParent.getModuleParent(), accessRightsInfo);

		}

		rolesInfo.setRoleId(roleId);
		
		AccessRightsMap accessRightsMapDetails = new AccessRightsMap();
		accessRightsMapDetails.setAccessRightsInfoMap(accessRightsMap);
		rolesInfo.setAccessRightsMap(accessRightsMapDetails);
		response.setData(rolesInfo);
		response.setStatus(200);
		return response;

	}

	public Response<MasterRolesListDTO> getMasterRoles(String userPortal, String companyid) {

		Response<MasterRolesListDTO> response = new Response<>();
		List<RolesMaster> rolesMaster = null;
		if ("IC".equalsIgnoreCase(userPortal) && StringUtils.isNotBlank(companyid)) {
			rolesMaster = rolesMasterRepo.findByCompanyId(companyid);
		} else {
			rolesMaster = rolesMasterRepo.findByUserportal(userPortal);
		}
		List<MasterRolesDTO> masterRoles = new ArrayList<>();
		for (RolesMaster roleMaster : rolesMaster) {
			MasterRolesDTO dto = new MasterRolesDTO();
			dto.setMasterRoleId(roleMaster.getMasterId());
			dto.setRoleName(roleMaster.getRole());
			masterRoles.add(dto);
		}

		MasterRolesListDTO data = new MasterRolesListDTO();
		data.setMasterRoles(masterRoles);
		response.setData(data);
		return response;
	}

}
