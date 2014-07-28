package com.ir.qa.parser.templateParser;
import java.io.IOException;
import java.util.Calendar;

public class DeathDateTemplateParser implements TemplateParser {

	public String parse(String value) throws IOException {
		String result = "";
		String result1 = "";

		value = value.replaceAll("[\\{]|[\\}]","");
		String split[];
		value = value.replaceAll("\\|df=[^\\|]*", "");
		value = value.replaceAll("\\|mf=[^\\|]*", "");
		//System.out.println("value:: "+value);
		split = value.split("\\|");
		int birthHypen=0;
		
		if(split[0].equalsIgnoreCase("death-date"))
		{
			if(split.length == 2)
			{
				
				if(split[1].contains(" "))
				{
					String[] temp = split[1].split(" ");
					String month = getMonth(temp[1]);
					if("".equalsIgnoreCase(month))
					{
						month = getMonth(temp[0]);
						result = temp[2]+" "+month+" "+temp[1];
					}
					else
						result = temp[2]+" "+month+" "+temp[0];
					
				}
				else
					result = split[1]+" 01 01";
				//	System.out.println("1 "+result);
					result = TemplateParserUtil.getDate(result);
				birthHypen=1;
				//System.out.println("XX::Formatted -:: "+result);
			}
				
			
			else
			{
				split[1] = split[1].replaceAll("\\,", "");
				//System.out.println(split[1]);
				if(split[1].contains(" "))
				{
					String[] temp = split[1].split(" ");
					String month = getMonth(temp[1]);
					if("".equalsIgnoreCase(month))
					{
						month = getMonth(temp[0]);
						result = temp[2]+" "+month+" "+temp[1];
					}
					else
						result = temp[2]+" "+month+" "+temp[0];
					
				birthHypen=1;
				}
				else
				{
					result = split[1];
					
					birthHypen=1;
				}
				//System.out.println("2 "+result);
				result = TemplateParserUtil.getDate(result);
				//System.out.println("XX::Formatted -:: "+result);
			}
		}
		if(split[0].equalsIgnoreCase("death-date and age"))
		{
			
				split[1] = split[1].replaceAll("\\,", "");
				split[2] = split[2].replaceAll("\\,", "");
				//System.out.println(split[1]);
				if(split[1].contains(" ") && split[2].contains(" "))
				{
					String birth="";
					String[] temp = split[1].split(" ");
					String month = getMonth(temp[1]);
					if("".equalsIgnoreCase(month))
					{
						month = getMonth(temp[0]);
						result = temp[2]+" "+month+" "+temp[1];
					}
					else
						result = temp[2]+" "+month+" "+temp[0];
					
					String[] temp1 = split[2].split(" ");
					String month1 = getMonth(temp1[1]);
					if("".equalsIgnoreCase(month1))
					{
						month1 = getMonth(temp1[0]);
						birth = temp1[2]+" "+month1+" "+temp1[1];
					}
					else
						birth = temp1[2]+" "+month1+" "+temp1[0];
					//System.out.println("BB "+birth+" value "+result);
					int x = findAge(birth,result);
					//System.out.println("3 "+result);
					result = TemplateParserUtil.getDate(result)+" | age = "+x;
					
					
				birthHypen=1;
				}
				else
				{
					result = split[1];
					
					birthHypen=1;
				}
				
				//System.out.println("XX::Formatted -:: "+result);
			
		}
		
		if(value.contains("date") && !(birthHypen == 1))
		{
			//death = reArrange(death);
			split = value.split("\\|");
			if(split.length > 0)
			{
				if(split[0].contains("age"))
				{
					
						if(split[3].equalsIgnoreCase("0") || split[3].equalsIgnoreCase("00"))
							split[3] = "1";
						if(split[2].equalsIgnoreCase("0") || split[2].equalsIgnoreCase("00"))
							split[2] = "1";
						result = split[3]+" "+split[2]+" "+split[1].trim();
						if(split[6].equalsIgnoreCase("0") || split[6].equalsIgnoreCase("00"))
							split[6] = "1";
						if(split[5].equalsIgnoreCase("0") || split[5].equalsIgnoreCase("00"))
							split[5] = "1";
						result1 = split[6]+" "+split[5]+" "+split[4].trim();
						//System.out.println(result+" "+result1);
						int age = findAge(result1,result);
						result = split[1].trim()+" "+split[2]+" "+split[3];
						//System.out.println("4 "+result);
						result = TemplateParserUtil.getDate(result)+" | age = "+age;
						//System.out.println("XXI::Formatted death date and age:: "+result+"\n");
				}
				else
				{
					if(!isNumeric(split[2]))
					{
						String month = getMonth(split[2]);
						result = split[1]+" "+month+" "+split[3];
					}
					else
						result = split[1]+" "+split[2]+" "+split[3];
					//System.out.println("5 "+result);
					result = TemplateParserUtil.getDate(result);
					//System.out.println("XX::Formatted death date:: "+result);
				}
			}
		}
		else if(value.contains("year") && !(birthHypen == 1))
		{
			split = value.split("\\|");
			if(split.length > 0)
			{
				if(split[0].contains("age"))
				{
					if(split.length == 3 || split.length == 4)
					{
						int age = Integer.parseInt(split[1]) - Integer.parseInt(split[2]);
						result = split[1]+" | aged = "+age;
					}
				}
			}
			//System.out.println("XX::Formatted death date:: "+result);
		}
		/*else if(death.contains("mf"))
		{
			death = reArrange(death);
			split = death.split("\\|");
			if(split.length > 0)
			{
				if(split[0].contains("age"))
				{
					
						result = split[3]+" "+split[2]+" "+split[1];
						//int age = findAge(result);
						//System.out.println("Formatted death date and age:: "+result+" "+age+"\n");
				}
				else
				{
					result = split[3]+" "+split[2]+" "+split[1];
					System.out.println("Formatted death date:: "+result);
				}
			}
		}*/
		
		else 
		{
			if(!(birthHypen == 1))
			{
				split = value.split("\\|");
				if(split.length > 0)
				{
					if(split[0].contains("age"))
					{
						
							result = split[1]+" "+split[2]+" "+split[3];
							//int age = findAge(result);
							//System.out.println("Formatted death date and age:: "+result+" "+age+"\n");
							//System.out.println("6 "+result);
							result = TemplateParserUtil.getDate(result);
							//System.out.println("XX2::Formatted death date:: "+result);
							//int age = findAge(result);
							//System.out.println("Formatted death date and age:: "+result+" "+age+"\n");
					}
					else
					{
						result = split[3]+" "+split[2]+" "+split[1];
						//System.out.println("7 "+result);
						result = TemplateParserUtil.getDate(result);
						//System.out.println("XX1::Formatted death date:: "+result);
					}
				}
			}
		}
		return result;
		}
		
	

	int findAge(String birth, String death) {
		int years = 0;
		int months = 0;
		int days = 0;
		 
		//System.out.println(birth+" "+death);
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
		 
		//System.out.println("The age is : "+years+" years");
		return years;
	}

	
	
	String getMonth(String month)
	{
		String[] months={"Summa","January","February","March","April","May","June","July","August","September","October","November","December"};
		String result ="";
		
		for(int i=0;i<months.length;i++)
		{
			if(month.equalsIgnoreCase(months[i]))
			{
				result += i;
				break;
			}
		}
		return result;
	}
	
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
}
