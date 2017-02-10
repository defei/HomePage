package org.codelogger.homepage.bean;

/**
 * 区域首页数据填充占位符
 */
public enum ProgramDataPlaceholder {

	/*全局约定*/
	DATA_SOURCE("数据来源,请设置为attribute","hp-source"),
	DATA_FIELD("子数据来源,请设置为attribute","hp-field"),
	REPEAT_ELEMENT("需要重复的元素,请设置为attribute","hp-repeat"),
	SHOP_FLAG("店铺标志占位符,请设置为attribute","gridy-shop-flag"),

		/*公共*/
		TEXT("text字段","#text#"),
		SECONDARY_TEXT("tsecondaryText字段","#secondaryText#"),
		IMG_URL("imgUrl字段","#imgUrl#"),
		STYLE_CLASS("styleClass字段","#styleClass#"),
		LINK("link字段","#link#"),
		ORDER_INDEX("orderIndex字段","#orderIndex#"),

	/*店铺相关*/
	SHOP_ID("店铺ID", "#shopId#"),
	SHOP_LOGO("店铺Logo", "#shopLogo#"),
	SHOP_NAME("店铺名称", "#shopName#"),
	SHOP_TAGS("店铺标签", "#shopTags#"),
	SHOP_SCOPE("服务介绍", "#shopScope#"),
	SHOP_LAST_ACTIVE_TIME("店铺最后活动时间", "#shopLastActiveTime#"),
	SHOP_AVG_SCORE("店铺平均分", "#shopAvgScore#"),
	SHOP_HALF_AVG_SCORE("店铺平均分除2", "#shopHalfAvgScore#"),
	SHOP_FLAG_KEY("店铺标识的key", "#shopFlagKey#"),
	SHOP_FLAG_NAME("店铺标识的名称", "#shopFlagName#"),
	SHOP_FLAG_BG_COLOR("店铺标识的背景颜色", "#shopFlagBgColor#"),

	/*群相关*/
	GROUP_ID("群ID", "#groupId#"),
	GROUP_NAME("群名称", "#groupName#"),
	GROUP_LOGO("群Logo", "#groupLogo#"),
	GROUP_BACKGROUND("群背景图", "#groupBackground#"),
	GROUP_TAGS("群标签", "#groupTags#"),
	GROUP_MEMBER_COUNT("群成员数", "#groupMemberCount#"),

	/*搜索相关*/
	Search_Placehollder("搜索框内点位符", "#searchPlaceholder#"),

	/*导航相关*/
	Navigation_Text("导航文字", "#navigationText#"),
	Navigation_Style_Class("导航文字", "#navigationStyleClass#"),
	Navigation_Link("导航链接", "#navigationLink#"),

		/*标签相关*/
		Label_Text("标签文字", "#labelText#"),


		/*秒杀相关*/
		Seckill_Title("秒杀标题", "#seckillTitle#"),
		Seckill_SecondaryText("秒杀副标题", "#seckillSecondaryText#"),
		Seckill_MoreImgUrl("更多秒杀图片地址","#seckillMoreImgUrl#"),
		Seckill_MoreLink("更多秒杀链接地址","#seckillMoreLink#"),

		/*橱窗展示*/
		Showcase_Text("标题","#showcaseTitle#"),
		Showcase_ImgUrl("图片地址","#showcaseImgUrl#"),
		Showcase_OriginPrice("原价","#showcaseOriginPrice#"),
		Showcase_SalePrice("现价","#showcaseSalePrice#"),
		Showcase_SoldPercent("现价","#showcaseSoldPercent#"),
		Showcase_Link("现价","#showcaseLink#"),




		A("","");


	private String description;

	private String placeholder;

	ProgramDataPlaceholder(String description, String placeholder) {
		this.description = description;
		this.placeholder = placeholder;
	}

	public String getDescription() {
		return description;
	}

	public String getPlaceholder() {
		return placeholder;
	}
}
