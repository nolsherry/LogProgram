package com.example.logprogram;

public class Constant {
	public static final int REGISTER_FLAG = 0;
	public static final int UNREGISTER_FLAG = 1;
	public static final int CHINESE_FLAG = 2;
	public static final int ENGLISH_FLAG = 3;
	public static final int c_login = 0;
	public static final int c_register = 1;
	public static final int c_unregister = 2;
	public static final int e_login = 3;
	public static final int e_register = 4;
	public static final int e_unregister = 5;
	public static boolean register_sucess = false;
	public static boolean resignation_sucess = false;
	public static boolean gps_flag;
	public static boolean wifi_flag;
	public static boolean mobile_flag;
	public static final String c_registerSuccess = "註冊成功!";
	public static final String c_registerFailure = "註冊失敗!請先檢查是否已開啟無線網路服務，若重新註冊仍無法成功，請來信通知\n服務信箱: nol.cs.nctu.edu.tw";
	public static final String e_registerSuccess = "Registration Success!";
	public static final String e_registerFailure = "Registration Fails! Please check if wireless service is enabled? If fails to successfully register after repeat, please contact us.\nemail: nol.cs.nctu.edu.tw";
	public static final String c_resignationSuccess = "註銷成功!";
	public static final String c_resignationFailure = "註銷失敗!請先檢查是否已開啟無線網路服務，若重新註銷仍無法成功，請來信通知\n服務信箱: nol.cs.nctu.edu.tw";
	public static final String e_resignationSuccess = "Resignation Success!";
	public static final String e_resignationFailure = "Resignation Fails! Please check if wireless service is enabled? If fails to successfully resign after repeat, please contact us.\nemail: nol.cs.nctu.edu.tw";
	public static final String c_login_fail = "登入失敗!請確認登入資訊填寫是否有誤，若為初次使用此應用程式，請務必點選'註冊帳號'，若尚有任何問題，請來信通知\n服務信箱: nol.cs.nctu.edu.tw";
	public static final String e_login_fail = "Login fails! Please ensure if login information is correct, if you are the first time to use the app, remember to click 'register'. Please contact us if there's any problems.\nemail: nol.cs.nctu.edu.tw";
}
