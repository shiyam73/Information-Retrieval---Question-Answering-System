package com.ir.qa.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ir.qa.QueryProcessor.SolrJSearcher;
import com.ir.qa.initializer.forServlet;

/**
 * Servlet implementation class LoginServlet
 */
public class MoreLikeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MoreLikeServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			String infobox_type = request.getParameter("infobox_type");
			String id = request.getParameter("id");
			JSONObject resultJSON = new JSONObject();
			// String password = request.getParameter("pwd");
			System.out.println("infobox_type"+infobox_type+"id"+id);
			Map<String,Map<String,String>> result = (Map<String,Map<String,String>>)SolrJSearcher.moreLikeThisFunction(id, infobox_type);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			if (result != null) {
				resultJSON.put("Result", result);
				System.out.println("result::" + result.toString());
				PrintWriter writer = response.getWriter();
				writer.print(resultJSON.toString());
			} else {
				response.getWriter().write("No results found");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
}