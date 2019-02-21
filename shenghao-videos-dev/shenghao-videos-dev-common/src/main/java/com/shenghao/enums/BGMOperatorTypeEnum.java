package com.shenghao.enums;

public enum BGMOperatorTypeEnum {
	
	ADD("1", "添加bgm"),				
	DELETE("2", "删除bgm");		
	
	public final String type;
	public final String value;
	
	BGMOperatorTypeEnum(String type, String value){
		this.type = type;
		this.value = value;
	}
	
	public String getUserType() {
		return type;
	}  
	
	public String getValue() {
		return value;
	}

    /**
     * 根据当前传入的key的值返回内容: 1是添加, 2是删除
	 * @param key
     * @return
     */
	public static String getValueByKey(String key) {
		for (BGMOperatorTypeEnum type : BGMOperatorTypeEnum.values()) {
			if (type.getUserType().equals(key)) {
				return type.value;
			}
		}
		return null;
	}
	
}
