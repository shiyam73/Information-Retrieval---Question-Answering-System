package com.ir.qa.parser.templateParser;
import java.io.IOException;
import java.util.Calendar;


public class BirthDateTemplateParser implements TemplateParser  {

	public String parse(String value)throws IOException {
		
		String result = "";
		int birthBased=0;

		
		String split[];
		
		int birthHypen=0;
		value = value.replaceAll("\\|df=[^\\|]*", "");
		value = value.replaceAll("\\|df =[^\\|]*", "");
		value = value.replaceAll("\\|mf=[^\\|]*", "");
		value = value.replaceAll("\\|mf =[^\\|]*", "");
		value = value.replaceAll("[\\{]|[\\}]","");
		//System.out.println("Birth12 :: "+value);
		split = value.split("\\|");
		
		if(split[0].equalsIgnoreCase("Birth based on age as of date"))
		{
			int age = Integer.parseInt(split[1]);
			int year = Integer.parseInt(split[2]);
			year -= age;
			age++;
			if(!value.contains("noage"))
				result = (year-1)+"/"+year+" age "+age+"\n";
			else
				result = (year-1)+"/"+year+"\n";
			//System.out.println(result);
			birthBased =1;
		}
		else if(split[0].equalsIgnoreCase("birth-date") && !value.contains("dt="))
		{
			if(split.length == 2)
			{
				if(split[1].contains("."))
				{
					split[1] = split[1].replaceAll("c.","").trim();
					result = split[1];
					
				}
				if(split[1].contains(" "))
				{
					split[1] = split[1].replaceAll("\\,", "");
					String[] temp = split[1].split(" ");
					String month = getMonth(temp[1]);
					if("".equalsIgnoreCase(month))
					{
						month = getMonth(temp[0]);
						if(temp.length==2)
						{
							result = temp[2]+" "+month+" 1";
							result = TemplateParserUtil.getDate(result);
						}
						else
						{
							result = temp[2]+" "+month+" "+temp[1];
							result = TemplateParserUtil.getDate(result);
						}
					}
					else
					{
						result = temp[2]+" "+month+" "+temp[0];
						result = TemplateParserUtil.getDate(result);
					}
					
				}
				else
				{
					result = split[1]+" 01 01";
					result = TemplateParserUtil.getDate(result);
				}
				birthHypen=1;
				//System.out.println("Formatted -:: "+result);
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
						result = TemplateParserUtil.getDate(result);
					}
					else
					{
						result = temp[2]+" "+month+" "+temp[0];
						result = TemplateParserUtil.getDate(result);
					}
					
					birthHypen=1;
				}
				else
				{
					result = split[1]+" 01 01";
					result = TemplateParserUtil.getDate(result);
					birthHypen=1;
				}
				//System.out.println("Formatted -:: "+result);
			}
		}
		if(value.contains("date") && !(birthBased == 1) && !(birthHypen == 1))
		{	
			
			//birth = reArrange(birth);
			
			if(split.length > 0)
			{
				if(split[0].contains("age"))
				{
					
						result = split[3]+" "+split[2]+" "+split[1];
						int age = findAge(result);
						result = split[1]+" "+split[2]+" "+split[3];
						result = TemplateParserUtil.getDate(result);
						
						result += " |age "+age;
						//System.out.println("Formatted Birth date and age:: "+result+" |age "+age+"\n");
				}
				else
				{
					if(split.length == 3)
						result = split[2]+" "+split[1];
					else if(split.length == 2)
					{
						result = split[1]+" 01 01";
						result = TemplateParserUtil.getDate(result);
					}
						
					else
					{
						String month = getMonth(split[2]);
						if("".equalsIgnoreCase(month))
						{
							result = split[1]+" "+split[2]+" "+split[3];
							result = TemplateParserUtil.getDate(result);
						}
						else
						{
							result = split[1]+" "+month+" "+split[3];
							result = TemplateParserUtil.getDate(result);
						}
					}
					//System.out.println("Formatted Birth date:: "+result+"\n");
				}
			}
		}
		else if(value.contains("year") && !(birthBased == 1) && !(birthHypen == 1))
		{
			int check=0;
			
			split = value.split("\\|");
			
			/*if(value.contains("df") || value.contains("mf"))
			{
				check = 1;
			}*/
			if(split.length > 0)
			{
				if(split[0].contains("age"))
				{
					
					/*if(check > 0)
					{
						if(split.length == 3)
						{
							result = "1 1 " +split[1];
							int age = findAge(result);
							System.out.println("Formatted Birth date and age:: "+age+"-"+(age+1)+"\n");
						}
						else if(split.length == 4)
						{
							result = "1 "+split[2]+" "+split[1];
							int age = findAge(result);
							System.out.println("Formatted Birth date and age:: "+age+"-"+(age+1)+"\n");
						}
					}*/
					
						
						if(split.length == 2)
						{
							result = "1 1 " +split[1];
							int age = findAge(result);
							result = age+"-"+(age+1);
							//System.out.println("Formatted Birth date and age:: "+age+"-"+(age+1)+"\n");
						}
						else if(split.length == 3)
						{
							result = "1 "+split[2]+" "+split[1];
							int age = findAge(result);
							result = age+"-"+(age+1);
							//System.out.println("Formatted Birth date and age:: "+age+"-"+(age+1)+"\n");
						}
					
			
				
			
		}
		
		else 
		{
			split = value.split("\\|");
			if(split.length > 0)
			{
				if(split[0].contains("age"))
				{
					
						result = split[3]+" "+split[2]+" "+split[1];
						int age = findAge(result);
						result = split[1]+" "+split[2]+" "+split[3];
						result = TemplateParserUtil.getDate(result);
						result +=" |age "+age;
						//System.out.println("Formatted Birth date and age:: "+result+" "+age);
				}
				else
				{
					result = split[1]+" "+split[2]+" "+split[3];
					result = TemplateParserUtil.getDate(result);
					//System.out.println("Formatted Birth date:: "+result);
				}
			}
		}
	
			}
			}
			//System.out.println("The birth date is:: "+result);
		return result;
	}
	
	int findAge(String birth)
	{
		//System.out.println("birth::"+birth);
		int years = 0;
		int months = 0;
		int days = 0;
		String temp[];
		
		temp = birth.split(" ");
		temp[1] = temp[1].replaceAll("0","");
		temp[2] = temp[2].replaceAll("0","");
		//System.out.println();
		//create calendar object for current day
		Calendar birthDay = Calendar.getInstance();
		if(temp.length>0)
		{
			
			birthDay.set(Calendar.YEAR, Integer.parseInt(temp[2]));
			birthDay.set(Calendar.MONTH, Integer.parseInt(temp[1]));
			birthDay.set(Calendar.DATE, Integer.parseInt(temp[0]));
		}
		 
		//create calendar object for current day
		long currentTime = System.currentTimeMillis();
		Calendar currentDay = Calendar.getInstance();
		currentDay.setTimeInMillis(currentTime);
		 
		//Get difference between years
		years = currentDay.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
		 
		 
		int currMonth = currentDay.get(Calendar.MONTH)+1;
		int birthMonth = birthDay.get(Calendar.MONTH)+1;
		 
		//Get difference between months
		months = currMonth - birthMonth;
		 
		//if month difference is in negative then reduce years by one and calculate the number of months. 
		if(months < 0)
		{
		 years--;
		 months = 12 - birthMonth + currMonth;
		  
		 if(currentDay.get(Calendar.DATE)<birthDay.get(Calendar.DATE))
		  months--;
		  
		}else if(months == 0 && currentDay.get(Calendar.DATE) < birthDay.get(Calendar.DATE)){
		 years--;
		 months = 11;
		}
		 
		 
		//Calculate the days
		if(currentDay.get(Calendar.DATE)>birthDay.get(Calendar.DATE)){
		 days = currentDay.get(Calendar.DATE) -  birthDay.get(Calendar.DATE);
		}
		else if(currentDay.get(Calendar.DATE)<birthDay.get(Calendar.DATE)){
		 int today = currentDay.get(Calendar.DAY_OF_MONTH); 
		 currentDay.add(Calendar.MONTH, -1);
		 days = currentDay.getActualMaximum(Calendar.DAY_OF_MONTH)-birthDay.get(Calendar.DAY_OF_MONTH)+today;
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

	
	

}
