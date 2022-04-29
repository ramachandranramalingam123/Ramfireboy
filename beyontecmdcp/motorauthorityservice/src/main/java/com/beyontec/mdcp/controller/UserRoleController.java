package com.beyontec.mdcp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beyontec.mdcp.dto.MasterRolesListDTO;
import com.beyontec.mdcp.dto.RolesInfo;
import com.beyontec.mdcp.dto.RolesInfoDto;
import com.beyontec.mdcp.dto.RolesModuleDto;
import com.beyontec.mdcp.dto.UserRolesDto;
import com.beyontec.mdcp.response.Response;
import com.beyontec.mdcp.service.UserRoleService;

@RestController
@RequestMapping("/userroles")
public class UserRoleController {

	@Autowired
	private UserRoleService userRoleService;

	@GetMapping("/showroles")
	public Response<Map<String, List<RolesModuleDto>>> getUsersModules(@RequestParam("portal") String portal) {

		return userRoleService.getUserModules(portal);
	}

	@PostMapping("/add")
	public Response<String> addOrEditRoles(@RequestBody UserRolesDto userRolesDto) {
		return userRoleService.addOrEditRoles(userRolesDto);

	}
	
	@GetMapping
	public Response<RolesInfoDto> getUserRoles(@RequestParam("pageSize") Integer pageSize,
			@RequestParam("currentPage") Integer currentPage, @RequestParam("userPortal") String userPortal,
			 @RequestParam("companyId") String companyId) {

		return userRoleService.getUserRoles(pageSize, currentPage, userPortal, companyId);
	}
	
	@GetMapping("/getRole")
	public Response<RolesInfo> getUsersRoleInfo(@RequestParam("roleId") String roleId, @RequestParam("userPortal") String userPortal) {

		return userRoleService.getRoleInfo(roleId, userPortal);
	}

	@GetMapping("/masterRoles")
	public Response<MasterRolesListDTO> getMasterList(@RequestParam("userPortal") String userPortal, @RequestParam("companyId") String companyId) {

		return userRoleService.getMasterRoles(userPortal, companyId);
	}
}
