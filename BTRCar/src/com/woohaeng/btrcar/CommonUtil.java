package com.woohaeng.btrcar;

public class CommonUtil {
	public static final String ENV_RUN_MODE = "run_mode";
	public static final String ENV_MAP_NAME = "map_name";
	public static final String ENV_ICON_NAME = "icon_name";
	public static final String ENV_DISP_TYPE = "disp_type";
	public static final String ENV_PHONE_NUMBER = "phone_number";
	public static final String ENV_START_TEXT = "start_text";
	
	public static final String ENV_HUD_USE1 = "use_yn";
	public static final String ENV_BLUETOOTH_NAME1 = "bluetooth_name";
	public static final String ENV_BLUETOOTH_ADDRESS1 = "bluetooth_address";
	public static final String ENV_HUD_TYPE1 = "hud_type";
	
	public static final String ENV_HUD_USE2 = "use_yn2";
	public static final String ENV_BLUETOOTH_NAME2 = "bluetooth_name2";
	public static final String ENV_BLUETOOTH_ADDRESS2 = "bluetooth_address2";
	public static final String ENV_HUD_TYPE2 = "hud_type2";
	
	// Intent extra
	public static final String EXTRA_RUN_MODE = "run_mode";
	public static final String EXTRA_MAP_NAME = "map_name";
	public static final String EXTRA_ICON_NAME = "icon_name";
	public static final String EXTRA_DISP_TYPE = "disp_type";
	public static final String EXTRA_PHONE_NUMBER = "phone_number";
	public static final String EXTRA_START_TEXT = "start_text";
	public static final String EXTRA_BLUETOOTH_NAME = "bluetooth_name";
	public static final String EXTRA_BLUETOOTH_ADDRESS = "bluetooth_address";
	public static final String EXTRA_HUD_TYPE = "hud_type";
	
	public static final String BOOT_COMPLETED = "BOOT_COMPLETED";
	public static final String START_SERVICE = "START_SERVICE";
	public static final String CONNECT_SERVICE = "CONNECT_SERVICE";
	public static final String CHANGE_CONFIG = "CHANGE_CONFIG";
	
	public static final int SERVICE_STATUS_NONE = 0;
	public static final int SERVICE_STATUS_OK = 1;
	public static final int SERVICE_STATUS_FAIL = 2;
	public static final int SERVICE_STATUS_FAIL_BT = 3;
	public static final int SERVICE_STATUS_FAIL_UDP = 4;
	
	public static final int ICON_LOAD = 0;
	public static final int ICON_INFO = 1;
	public static final int ICON_DATA = 2;
	public static final int ICON_ERROR = 3;
	
	public static final int BT_1 = 1;
	public static final int BT_2 = 2;
	public static final int BT_ALL = 3;
	
	
	// Intent request codes
	public static final int REQUEST_CONNECT_DEVICE = 1;
	
    public static String rPad(String data, int size, String what){
        String tmp = "";
        if (data == null) {
            tmp = "";
        } else {
            int csize = (data.trim()).getBytes().length;

            if (csize <= size) {
                int iter = size - csize;
                for (int i = 0; i < iter; i++) {
                    tmp = tmp + what;
                }
                tmp = data.trim() + tmp;
            } else {
                tmp = (data.trim()).substring(0, size);
            }
        }
        return tmp;
    }
    
    public static String makeTelnoShortFormat(String telno12) throws Exception{
        String telno = "";
        
        if( telno12 != null){
            telno12 = telno12.trim();
            int telnoLength = telno12.length();
    
            if (telnoLength == 12 && telno12.substring(3, 5).equals("00")) {
                telno = telno12.substring(0, 3) + telno12.substring(5, 12);
            } else if (telnoLength == 12 && telno12.substring(3, 4).equals("0")) {
                telno = telno12.substring(0, 3) + telno12.substring(4, 12);
            } else if (telnoLength == 11 && telno12.substring(3, 4).equals("0")) {
                telno = telno12.substring(0, 3) + telno12.substring(4, 11);
            } else {
                telno = telno12;
            }
        }

        return telno;
    }
    
    public static String nullToEmpty(String data) {
    	if (data == null) {
    		return "";
    	}
    	
    	return data;
    }
}
