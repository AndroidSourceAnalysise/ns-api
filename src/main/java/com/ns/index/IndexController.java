package com.ns.index;

import com.jfinal.core.Controller;

/**
 * 本 ns 仅表达最为粗浅的 jfinal 用法，更为有价值的实用的企业级用法
 * 详见 JFinal 俱乐部: http://jfinal.com/club
 * 
 * IndexController
 */
public class IndexController extends Controller {
	public void index() {
		//render("index.html");
		renderText("hello !");
	}
}



