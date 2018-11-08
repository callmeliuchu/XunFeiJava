package com.iflytek.msp.lfasr;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;
import java.util.regex.Pattern;


import com.alibaba.fastjson.JSON;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.iflytek.msp.cpdb.lfasr.model.ProgressStatus;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.util.CommonUtil;


public class TestLfasr {

//	private static final String local_file = "/home/liuchu/Desktop/96c7cff20d7843b3be9ef6476b16b34c.mp3";

	private static final LfasrType type = LfasrType.LFASR_STANDARD_RECORDED_AUDIO;

	private static int sleepSecond = 20;

	public static String parseVideo(String local_file) {
		LfasrClientImp lc = null;
		try {
			lc = LfasrClientImp.initLfasrClient();
		} catch (LfasrException e) {

			Message initMsg = JSON.parseObject(e.getMessage(), Message.class);
			System.out.println("ecode=" + initMsg.getErr_no());
			System.out.println("failed=" + initMsg.getFailed());
		}


		String task_id = "";
		HashMap<String, String> params = new HashMap<>();
		params.put("has_participle", "true");
		try {

			Message uploadMsg = lc.lfasrUpload(local_file, type, params);


			int ok = uploadMsg.getOk();
			if (ok == 0) {

				task_id = uploadMsg.getData();
				System.out.println("task_id=" + task_id);
			} else {

				System.out.println("ecode=" + uploadMsg.getErr_no());
				System.out.println("failed=" + uploadMsg.getFailed());
			}
		} catch (LfasrException e) {

			Message uploadMsg = JSON.parseObject(e.getMessage(), Message.class);
			System.out.println("ecode=" + uploadMsg.getErr_no());
			System.out.println("failed=" + uploadMsg.getFailed());
		}


		while (true) {
			try {

				Thread.sleep(sleepSecond * 1000);
				System.out.println("waiting ...");
			} catch (InterruptedException e) {
			}
			try {

				Message progressMsg = lc.lfasrGetProgress(task_id);


				if (progressMsg.getOk() != 0) {
					System.out.println("task was fail. task_id:" + task_id);
					System.out.println("ecode=" + progressMsg.getErr_no());
					System.out.println("failed=" + progressMsg.getFailed());


					break;
				} else {
					ProgressStatus progressStatus = JSON.parseObject(progressMsg.getData(), ProgressStatus.class);
					if (progressStatus.getStatus() == 9) {
						// ???????
						System.out.println("task was completed. task_id:" + task_id);
						break;
					} else {
						// ????????
						System.out.println("task was incomplete. task_id:" + task_id + ", status:" + progressStatus.getDesc());
					}
				}
			} catch (LfasrException e) {

				Message progressMsg = JSON.parseObject(e.getMessage(), Message.class);
				System.out.println("ecode=" + progressMsg.getErr_no());
				System.out.println("failed=" + progressMsg.getFailed());
				break;
			}

		}


		try {
			Message resultMsg = lc.lfasrGetResult(task_id);
			System.out.println(resultMsg.getData());

			if (resultMsg.getOk() == 0) {

				System.out.println(resultMsg.getData());
				return resultMsg.getData();
			} else {

				System.out.println("ecode=" + resultMsg.getErr_no());
				System.out.println("failed=" + resultMsg.getFailed());
			}
		} catch (LfasrException e) {

			Message resultMsg = JSON.parseObject(e.getMessage(), Message.class);
			System.out.println("ecode=" + resultMsg.getErr_no());
			System.out.println("failed=" + resultMsg.getFailed());
		}
		return null;
	}

	public static List<String> getFiles(String path){
		List<String> files = new ArrayList<String>();
		File file = new File(path);
		File[] tempList = file.listFiles();

		for(int i=0;i<tempList.length;i++){
			if(tempList[i].isFile()){
				files.add(tempList[i].toString());
			}
		}
		return files;
	}

	public static List<List<String>> readCSV(String filePath) throws Exception{
		File file = new File(filePath);
		FileReader fileReader = new FileReader(file);
		CSVReader csvRerader = new CSVReader(fileReader);
		List res = new ArrayList<>();
		for(String[] ss : csvRerader.readAll()){
			List tmpList = new ArrayList<>();
			for(String s : ss){
				tmpList.add(s);
				System.out.print(s+",");
			}
			res.add(tmpList);
			System.out.println();
		}
		return res;
	}
	public static boolean isDouble(String str) {
		if (null == str || "".equals(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
		return pattern.matcher(str).matches();
	}


	public static void writeCSV(String fileName,List<String> videos) throws Exception{
		File file = new File(fileName);
		Writer writer = new FileWriter(file);
		CSVWriter csvWriter = new CSVWriter(writer,',');
//		ArrayList<String> videos = getFiles("/home/liuchu/Java_LongFormASR_2.0.0001.0/lfasr-sdk-demo/tmp");
		for(int i=0;i<videos.size();i++){
			String path = videos.get(i);
			String[] arr = path.split("/");
			String res = parseVideo(path);
			csvWriter.writeNext(new String[]{arr[arr.length-1],res});
//			System.out.println(path);
			System.out.println(res);
		}
	}


	public static void writeText(String dirName,List<String> videos) throws Exception{
		Set<String> alreadSet = CommonUtil.getAlreadyData(dirName);
		for(int i=0;i<videos.size();i++){
			String path = videos.get(i);
			String[] arr = path.split("/");
			String fileName = arr[arr.length-1];
			String pid = fileName.split("\\.")[0];
			if(alreadSet.contains(pid)){
				print("already exist ");
				continue;
			}
			String res = parseVideo(path);
			File file = new File(dirName+"/"+fileName);
			Writer writer = new FileWriter(file);
			writer.write(res);
			writer.close();
			System.out.println(res);
		}
	}


	public static boolean  writeVideoToText(String videoPath,String targetDir) throws Exception{
		Set<String> alreadySet = CommonUtil.getAlreadyData(targetDir);
		String[] arr = videoPath.split("/");
		String fileName = arr[arr.length-1];
		String pid = fileName.split("\\.")[0];
		if(!alreadySet.contains(pid)){
		    String res = parseVideo(videoPath);
			File file = new File(targetDir+"/"+fileName);
			Writer writer = new FileWriter(file,true);
			writer.write(res);
			writer.close();
			return true;
		}else{
			print(pid + "  already exist  ");
			return false;
		}
	}

	public static void writeTextByNum(List<String> videos,String targetDir,int num) throws Exception{
		int count = 0;
		for(int i=0;i<videos.size();i++){
			String path = videos.get(i);
			boolean isWrite = writeVideoToText(path,targetDir);
			if(isWrite){
				count += 1;
			}
			if(count >= num){
				break;
			}
		}
	}



	public static void showResultOfCSV(List<List<String>> list){
		for(List<String> ss : list){
			StringBuffer sb = new StringBuffer();
			for(String s : ss){
				sb.append(s).append(",");
			}
			print(sb.toString());
		}
	}

	public static Map<String,Float> makeMap(List<List<String>>list){
		Map<String,Float>hm = new HashMap<>();
		for(List<String> ss : list){
			String value = ss.get(1);
			if(isDouble(value)) {
				hm.put(ss.get(0),Float.valueOf(value));
			}
		}
		return hm;
	}

	public static List<String> generateCustomerDataset(List<String> videos) throws Exception{
		List<String>res = new ArrayList<>();
		List<List<String>>result =  readCSV("count_data.csv");
		Map<String,Float>map = makeMap(result);
		for(String s : videos){
			String[] arr = s.split("/");
			String pid = arr[arr.length-1].split("\\.")[0];
			if(map.containsKey(pid)){
				if(map.get(pid)<300){
					res.add(s);
				}
//				print(s+">>>>"+(map.get(pid)/60));
			}
		}
		return res;
	}





	public static void print(Object obj){
		System.out.println(obj);
	}

	public static void writeExampleText(String dirName,String targetName) throws Exception{
		List<String> videos = getFiles("/home/liuchu/Java_LongFormASR_2.0.0001.0/lfasr-sdk-demo/"+dirName);
		writeText(targetName,videos);
	}

	public static void writeExampleTextByNum(String dirName,String targetName,int num) throws Exception{
		List<String> videos = getFiles("/home/liuchu/Java_LongFormASR_2.0.0001.0/lfasr-sdk-demo/"+dirName);
		writeTextByNum(videos,targetName,num);
	}

	public static void writeExampleCSV(String dirName,String targetName) throws Exception{
		List<String> videos = getFiles("/home/liuchu/Java_LongFormASR_2.0.0001.0/lfasr-sdk-demo/"+dirName);
//		videos = generateCustomerDataset(videos);
		writeCSV(targetName,videos);
	}


	public static void main(String[] args) throws Exception{

		writeExampleTextByNum("top_hot_videos_mp3_30_60","translate_text_test",100);
//		writeVideoToText("/home/liuchu/Java_LongFormASR_2.0.0001.0/lfasr-sdk-demo/top_hot_videos_mp3_30_60/0a134316-d436-4ba3-906e-4f03906ce530.mp3",
//				"translate_text_test");


//		writeExampleText("top_hot_videos_mp3","top_hot_videos_text");
//		String res = parseVideo("/home/liuchu/Java_LongFormASR_2.0.0001.0/lfasr-sdk-demo/top_hot_videos_1/17c9fcfb-eb75-4c9d-92e1-23f8f108e5f7.mp3");
//		print(res);
//		writeExampleCSV("hot_mp3_120","hot_mp3_120.csv");
//		readCSV("customer_result.csv");

//		for(String s : videos){
//			String[] arr = s.split("/");
//			String pid = arr[arr.length-1].split("\\.")[0];
//			if(map.containsKey(pid)){
//				print(s+">>>>"+(map.get(pid)/60));
//			}
//		}


//		writeCSV("result.csv",videos);
//		readCSV("result.csv");
//		System.out.println(videos);

//		String str = "/home/liuchu/Desktop/96c7cff20d7843b3be9ef6476b16b34c.mp3";
//		String[] arr = str.split("/");
//		System.out.println(arr[arr.length-1]);
//
//		String res = parseVideo("/home/liuchu/Desktop/96c7cff20d7843b3be9ef6476b16b34c.mp3");
//		System.out.println("===============================================");
//		System.out.println(res);
//		System.out.println("===============================================");
	}
}
