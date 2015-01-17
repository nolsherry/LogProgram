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
	public static final String c_registerSuccess = "���U���\!";
	public static final String c_registerFailure = "���U����!�Х��ˬd�O�_�w�}�ҵL�u�����A�ȡA�Y���s���U���L�k���\�A�ШӫH�q��\n�A�ȫH�c: nol.cs.nctu.edu.tw";
	public static final String e_registerSuccess = "Registration Success!";
	public static final String e_registerFailure = "Registration Fails! Please check if wireless service is enabled? If fails to successfully register after repeat, please contact us.\nemail: nol.cs.nctu.edu.tw";
	public static final String c_resignationSuccess = "���P���\!";
	public static final String c_resignationFailure = "���P����!�Х��ˬd�O�_�w�}�ҵL�u�����A�ȡA�Y���s���P���L�k���\�A�ШӫH�q��\n�A�ȫH�c: nol.cs.nctu.edu.tw";
	public static final String e_resignationSuccess = "Resignation Success!";
	public static final String e_resignationFailure = "Resignation Fails! Please check if wireless service is enabled? If fails to successfully resign after repeat, please contact us.\nemail: nol.cs.nctu.edu.tw";
	public static final String c_login_fail = "�n�J����!�нT�{�n�J��T��g�O�_���~�A�Y���즸�ϥΦ����ε{���A�аȥ��I��'���U�b��'�A�Y�|��������D�A�ШӫH�q��\n�A�ȫH�c: nol.cs.nctu.edu.tw";
	public static final String e_login_fail = "Login fails! Please ensure if login information is correct, if you are the first time to use the app, remember to click 'register'. Please contact us if there's any problems.\nemail: nol.cs.nctu.edu.tw";
}
