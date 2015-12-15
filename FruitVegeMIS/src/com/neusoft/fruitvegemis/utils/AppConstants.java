package com.neusoft.fruitvegemis.utils;

public class AppConstants {
	public static String DBNAME = "fruitvg";
	public static int DBVERSION = 1;

	public static class TBUser {
		public static String name = "username";

		public static class Cloum {
			public static String id = "_id";
			public static String uname = "uname";
			public static String password = "password";
			public static String type = "type";
		}
	}

	public static class TBUOrder {
		public static String name = "user";

		public static class Cloum {
			public static String id = "_id";
			public static String uname = "uname";
			public static String type = "type";
			public static String oid = "oid";
			public static String ostate = "ostate";
			public static String oprice = "oprice";
			public static String odate = "odate";
		}
	}

	public static class TBOrder {
		public static String name = "ordertb";

		public static class Cloum {
			public static String id = "_id";
			public static String oid = "oid";
			public static String gname = "gname";
			public static String gprice = "gprice";
		}
	}

	public static class TBSGoods {
		public static String name = "SGoods";

		public static class Cloum {
			public static String id = "_id";
			public static String sname = "sname";
			public static String gname = "gname";
			public static String gprice = "gprice";
		}
	}
}
