package com.beyontec.mdcp.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.beyontec.mdcp.dto.BranchDataDto;
import com.beyontec.mdcp.dto.CompanyBranchDto;
import com.beyontec.mdcp.model.CompanyBranch;
import com.beyontec.mdcp.model.CompanyUserBranch;
import com.beyontec.mdcp.model.InsuranceCompany;
import com.beyontec.mdcp.model.User;
import com.beyontec.mdcp.repo.CompanyBranchRepo;
import com.beyontec.mdcp.repo.CompanyRepo;
import com.beyontec.mdcp.repo.CompanyUserBranchRepo;
import com.beyontec.mdcp.repo.UserRepo;
import com.beyontec.mdcp.response.Response;
import com.beyontec.mdcp.util.MotorAuthorityConstants;

@Service
public class CompanyBranchService {
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private CompanyUserBranchRepo companyUserBranchRepo;

	@Autowired
	private CompanyRepo companyRepo;
	
	@Autowired
	private CompanyBranchRepo companyBranchRepo;

	public Response<BranchDataDto> getCompanyUserBranchDetailsPage(Integer userId, Integer pageSize, Integer currentPage) {
		Response<BranchDataDto> response = new Response();
		List<CompanyBranchDto> companyBranchDtoList = new ArrayList<>();
		BranchDataDto branchDataDto = new BranchDataDto();
		User user = userRepo.findByUserId(userId);

		List<CompanyUserBranch> companyUserBranchList = new ArrayList<>();
		if(user == null) {
			response.setStatus(200);
			response.setMessage(MotorAuthorityConstants.COMPANY_USER_NOT_FOUND);
			return response;	
		}else {
		
			if ("A".equalsIgnoreCase(user.getBranchUserType())) {
				InsuranceCompany company = companyRepo.findByCompanyId(user.getCompanyId());
				List<CompanyBranch> compBranchList = companyBranchRepo.findByCompany(company);

				companyUserBranchList = companyUserBranchRepo.findByCompanyBranchIn(compBranchList);
			} else {
				companyUserBranchList = companyUserBranchRepo.findByUser(user);
			}
			
			List<String> strList = new ArrayList<>();
			String branchName;
			CompanyBranchDto  companyBranchDto  = null;
			for (CompanyUserBranch companyUserBranch : companyUserBranchList) {
				if (companyUserBranch.getCompanyBranch() != null && companyUserBranch.getCompanyBranch().getBranch() !=null) {
					branchName = companyUserBranch.getCompanyBranch().getBranch().getBranchName();
					if (org.apache.commons.lang.StringUtils.isNotBlank(branchName) && !strList.contains(branchName)) {
						strList.add(branchName);
						companyBranchDto  = new CompanyBranchDto();
						 companyBranchDto.setBranchId(companyUserBranch.getCompanyBranch().getCompanyBranchId());
						 companyBranchDto.setBranchName(companyUserBranch.getBranch().getBranchName());
						 companyBranchDtoList.add(companyBranchDto);
					}
				}
			}
			if (!StringUtils.isEmpty(pageSize) && !StringUtils.isEmpty(currentPage)) {
				int toIndex = currentPage * pageSize + pageSize;
				if (toIndex > companyBranchDtoList.size()) {
					toIndex = companyBranchDtoList.size();
				}
				branchDataDto.setCompanyBranch(companyBranchDtoList.subList(currentPage * 5, toIndex));
			} else {
				branchDataDto.setCompanyBranch(companyBranchDtoList);
			}
			branchDataDto.setTotalElements((long)companyBranchDtoList.size());
		}
		response.setData(branchDataDto);
		response.setStatus(200);
		response.setMessage(MotorAuthorityConstants.COMPANY_BRANCH_DATA);
		return response;
	}
	
	
	public Response<List<CompanyBranchDto>> getCompanyUserBranchDetails(Integer userId) {
		Response<List<CompanyBranchDto>> response = new Response();
		List<CompanyBranchDto> companyBranchDtoList = new ArrayList<>();
		User user = userRepo.findByUserId(userId);
		
		if(user == null) {
			response.setStatus(200);
			response.setMessage(MotorAuthorityConstants.COMPANY_USER_NOT_FOUND);
			return response;	
		}else {
			List<CompanyUserBranch> companyUserBranchList = new ArrayList<>();
			if ("A".equalsIgnoreCase(user.getBranchUserType())) {
				InsuranceCompany company = companyRepo.findByCompanyId(user.getCompanyId());
				List<CompanyBranch> compBranchList = companyBranchRepo.findByCompany(company);

				companyUserBranchList = companyUserBranchRepo.findByCompanyBranchIn(compBranchList);
			} else {
				companyUserBranchList = companyUserBranchRepo.findByUser(user);
			}
			List<String> strList = new ArrayList<>();
			String branchName;
			CompanyBranchDto  companyBranchDto  = null;
			for (CompanyUserBranch companyUserBranch : companyUserBranchList) {
				if (companyUserBranch.getCompanyBranch() != null && companyUserBranch.getCompanyBranch().getBranch() !=null) {
					branchName = companyUserBranch.getCompanyBranch().getBranch().getBranchName();
					if (org.apache.commons.lang.StringUtils.isNotBlank(branchName) && !strList.contains(branchName)) {
						strList.add(branchName);
						companyBranchDto  = new CompanyBranchDto();
						 companyBranchDto.setBranchId(companyUserBranch.getCompanyBranch().getCompanyBranchId());
						 companyBranchDto.setBranchName(companyUserBranch.getBranch().getBranchName());
						 companyBranchDtoList.add(companyBranchDto);
					}
				}
			}
			
		}
		response.setData(companyBranchDtoList);
		response.setStatus(200);
		response.setMessage(MotorAuthorityConstants.COMPANY_BRANCH_DATA);
		return response;
	}


}
