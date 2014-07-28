package com.ir.qa.parser.templateParser;
import java.io.IOException;
import java.util.Calendar;


public class BirthDeathAgeTemplateParser implements TemplateParser {

	@Override
	public String parse(String value) throws IOException {
		// TODO Auto-generated method stub
		String result = "";
		String split[];
		value = value.replaceAll("\\|df=[^\\|]*", "");
		value = value.replaceAll("\\|mf=[^\\|]*", "");
		value = value.replaceAll("[\\{]|[\\}]","");
		//System.out.println("Birth12 :: "+value);
		split = value.split("\\|");
		
		if(split[1].equalsIgnoreCase("B"))
		{
			String temp1="",temp2="";
			result = split[2]+" ";
			if(" ".equalsIgnoreCase(split[4]) && " ".equalsIgnoreCase(split[3]))
			{
				temp1 = "1 1 ";
				result += "1 1 ";
			}
			else
			{
				temp1 = split[4]+" "+split[3]+" ";
				result += split[4]+" "+split[3]+" ";
			}
			temp1 += split[2];
			temp2 += split[7]+" "+split[6]+" "+split[5];
			result = TemplateParserUtil.getDate(result);
			result = result+" age "+findAge(temp1,temp2);
			
			
		}
		else
		{
			String temp1="",temp2="";
			result = split[2]+" ";
			if(" ".equalsIgnoreCase(split[4]) && " ".equalsIgnoreCase(split[3]))
			{
				temp1 = "1 1 ";
				result += "1 1 ";
			}
			else
			{
				temp1 = split[4]+" "+split[3]+" ";
				result += split[4]+" "+split[3]+" ";
			}
			temp1 += split[2];
			temp2 += split[7]+" "+split[6]+" "+split[5];
			result = TemplateParserUtil.getDate(result);
			result = split[5]+" age "+findAge(temp1,temp2);
			
			
		}
		
		return result;
	}

	String findAge(String birth,String death)
	{
		int years = 0;
		int months = 0;
		int days = 0;
		
		String temp[];
		String temp1[];
		
		temp = birth.split(" ");
		temp[0] = temp[0].replaceAll("0","");
		temp[1] = temp[1].replaceAll("0","");
		
		temp1 = death.split(" ");
		temp1[0] = temp1[0].replaceAll("0","");
		temp1[1] = temp1[1].replaceAll("0","");
		
		//create calendar object for current day
		Calendar birthDay = Calendar.getInstance();
		if(temp.length>0)
		{
			
			birthDay.set(Calendar.YEAR, Integer.parseInt(temp[2]));
			birthDay.set(Calendar.MONTH, Integer.parseInt(temp[1]));
			birthDay.set(Calendar.DATE, Integer.parseInt(temp[0]));
		}
		
		Calendar deathDay = Calendar.getInstance();
		if(temp1.length>0)
		{
			
			deathDay.set(Calendar.YEAR, Integer.parseInt(temp1[2]));
			deathDay.set(Calendar.MONTH, Integer.parseInt(temp1[1]));
			deathDay.set(Calendar.DATE, Integer.parseInt(temp1[0]));
		}
		 
		//create calendar object for current day
		long currentTime = System.currentTimeMillis();
		Calendar currentDay = Calendar.getInstance();
		currentDay.setTimeInMillis(currentTime);
		 
		//Get difference between years
		years = deathDay.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
		 
		 
		int deathMonth = deathDay.get(Calendar.MONTH)+1;
		int birthMonth = birthDay.get(Calendar.MONTH)+1;
		 
		//Get difference between months
		months = deathMonth - birthMonth;
		 
		//if month difference is in negative then reduce years by one and calculate the number of months. 
		if(months < 0)
		{
		 years--;
		 months = 12 - birthMonth + deathMonth;
		  
		 if(deathDay.get(Calendar.DATE)<birthDay.get(Calendar.DATE))
		  months--;
		  
		}else if(months == 0 && deathDay.get(Calendar.DATE) < birthDay.get(Calendar.DATE)){
		 years--;
		 months = 11;
		}
		 
		 
		//Calculate the days
		if(deathDay.get(Calendar.DATE)>birthDay.get(Calendar.DATE))
		 days = deathDay.get(Calendar.DATE) -  birthDay.get(Calendar.DATE);
		else if(deathDay.get(Calendar.DATE)<birthDay.get(Calendar.DATE)){
		 int today = deathDay.get(Calendar.DAY_OF_MONTH); 
		 deathDay.add(Calendar.MONTH, -1);
		 days = deathDay.getActualMaximum(Calendar.DAY_OF_MONTH)-birthDay.get(Calendar.DAY_OF_MONTH)+today;
		}else{
		 days=0;
		  
		 if(months == 12){
		  years++;
		  months = 0;
		 }
		}
		 
		String result = years+" years, "+days+" days";
		return result;
	}

	
}
