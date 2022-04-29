package com.beyontec.mdcp.model;

import java.util.List;
import java.util.Map;

import com.beyontec.mdcp.dto.LabelAccessRightsInfo;

import lombok.Data;

@Data
public class AccessRightsMap {

	private Map<String, List<LabelAccessRightsInfo>> accessRightsInfoMap;

}
