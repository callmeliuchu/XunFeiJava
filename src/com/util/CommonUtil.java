package com.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class CommonUtil {
    public static Set<String> getAlreadyData(String filePath){
        Set<String> set = new HashSet<>();
        List<String>files = CommonUtil.getFiles(filePath);
        for(String s : files){
            String[] arr = s.split("/");
            String pid = arr[arr.length-1].split("\\.")[0];
            set.add(pid);
        }
        return set;
    }

    public static void print(Object obj){
        System.out.println(obj);
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

    public static List<String []> getCSVList(String filePath)throws Exception{
        File file = new File(filePath);
        FileReader fileReader = new FileReader(file);
        CSVReader csvRerader = new CSVReader(fileReader);
        List<String []>csvList = csvRerader.readAll();
        return csvList;
    }

    public static Map<String,String> getTsvMap(String filePath)throws Exception{
        Map<String,String> res = new HashMap<>();
        List<String []>csvList = getCSVList(filePath);
        for(String[] row : csvList){
            List<String> list = parseRow(row,true);
            res.put(list.get(0),list.get(1));
        }
        return res;
    }


    public static List<List<String>> readCSVList(List<String []>csvList,boolean isTsv,int start,int end) throws Exception{
        List res = new ArrayList<>();
        if(end>csvList.size()){
            end = csvList.size();
        }
        for(int i=start;i<end;i++){
            String[] ss = csvList.get(i);
//            print("-----------------------");
//            print(ss[0].split("\t")[0]);
//            print("-----------------------");
            List tmpList = parseRow(ss,isTsv);
            res.add(tmpList);
        }
        return res;
    }

    public static List<String> parseRow(String[] ss,boolean isTsv){
        List tmpList = new ArrayList<>();
        String[] arr = ss;
        if(isTsv){
            arr = ss[0].split("\t");
        }
        for(String s : arr){
            tmpList.add(s);
        }
        return tmpList;
    }


    public static boolean isDouble(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
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



    public static Map<String,List<String>> makeListMap(List<List<String>>list){
        Map<String,List<String>>hm = new HashMap<>();
        for(List<String> ss : list){
            hm.put(ss.get(0),ss);
        }
        return hm;
    }

    public static Map<String,List<String>> makeListMap(List<String []>csvList,boolean isTsv){
        Map<String,List<String>>hm = new HashMap<>();
        for(String[] ss: csvList){
            List<String> tmpList= parseRow(ss,isTsv);
            hm.put(tmpList.get(0),tmpList);
        }
        return hm;
    }

    public static String execCommand(String command){
        StringBuffer sb = new StringBuffer();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command);
            InputStream stderr = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                sb.append(line);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    public static String cut(String inFile, String outFile,String startTime,String duration) {
        String  command = " ffmpeg -ss "+startTime+" -t "+duration+"  -accurate_seek -i "+inFile+" -codec copy "+outFile;
        return execCommand(command);
    }

    public static String transfer(String inFile,String outFile){
        String command = "ffmpeg -i "+inFile+" "+outFile;
        return execCommand(command);
    }

    public static String count(String inFile){
        String command = "ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 " + inFile;
        return execCommand(command);
    }


    public static void transferFiles(String inputDir,String outputDir){
        Set<String> alreadSet = getAlreadyData(outputDir);
        List<String> fileList = getFiles(inputDir);
        for(String inpath : fileList){
//            float time_num = Float.valueOf(count(inpath));
            String fileName = inpath.split("/")[1];
            String[] arr = fileName.split("\\.");
            String pid = arr[0];
            if(alreadSet.contains(pid)){
                print(pid + " already exists");
                continue;
            }
            fileName = pid + ".mp3";
            String outputPath = outputDir + "/"+fileName;
//            cut(inpath,outputPath,"00:00:00","10");
//            if(time_num >= 1800 && time_num <= 3600){
                transfer(inpath,outputPath);
//            }
        }
    }



    public static void cutFiles(String inputDir,String outputDir,String duration){
        List<String> fileList = getFiles(inputDir);
        for(String inpath : fileList){
            String fileName = inpath.split("/")[1];
            String[] arr = fileName.split("\\.");
//            fileName = arr[0] + ".mp3";
            String outputPath = outputDir + "/"+fileName;
            cut(inpath,outputPath,"00:00:10",duration);
        }
    }

    public static Set makeListSet(List<List<String>>lists,int idx){
        Set<String> hotVideoSet = new HashSet<>();
        for(List<String> list : lists){
            String pid = list.get(idx);
            hotVideoSet.add(pid);
        }
        return hotVideoSet;
    }

    public static List makeListList(List<List<String>>lists,int idx){
        List<String> retList = new LinkedList<>();
        for(List<String> list : lists){
            String pid = list.get(idx);
            retList.add(pid);
        }
        return retList;
    }

    public static boolean deleteFile(String path){
        File file = new File(path);
        if(file.delete()){
            return true;
        }else{
            return false;
        }
    }

    public static void loadVideos(String saveDir) throws Exception{
        List<String []> topHotVideosCSVList = getCSVList("hot_videos_top.tsv");
        print("=================tophotvideos length is =========="+topHotVideosCSVList.size());
        List<String []> baiduCSVList = getCSVList("count.tsv");
        List<List<String>> baiduList = readCSVList(baiduCSVList,true,0,50000);
        print("=================baiduvideos length is =========="+baiduCSVList.size());
        Map<String,List<String>> baiduListMap = makeListMap(baiduList);
        Set<String> alreadyLoadedData = getAlreadyData(saveDir);
        int count = 0;
        print("=======================================================");
        for(String [] topHotVideoRow: topHotVideosCSVList){
            try {
                List<String> topHotVideo = parseRow(topHotVideoRow,true);
                String pid = topHotVideo.get(0);
                String readCount = topHotVideo.get(2);
                if(!alreadyLoadedData.contains(pid) && baiduListMap.containsKey(pid)){
                    List<String> abaiduList = baiduListMap.get(pid);
                    String bucketName = abaiduList.get(4);
                    String bucketKey = abaiduList.get(5);
                    String type = bucketKey.split("\\.")[1];
                    String savePath = saveDir + "/" + pid + "."+readCount+ type;
                    BaiduUtil.saveBucketContent(bucketName, bucketKey, savePath);
                    count += 1;
                    float time_num = Float.valueOf(count(savePath));
                    if(time_num < 1200 || time_num > 4000){
                        deleteFile(savePath);
                        count -= 1;
                    }
                    print(topHotVideo+"++++++++++++");
                    print(readCount);
                    if(count > 200){
                        break;
                    }
                }
            }catch(Exception e){
            }
        }

    }

    public static void deleteVideos(){
        Set<String> alreadyLoadedData = getAlreadyData("translate_text_test");
        String[] fileNameArr = new String[]{"top_hot_videos_mp3_30_60","top_hot_videos_30_60"};
        for(String fileName : fileNameArr) {
            List<String> deleteList = getFiles(fileName);
            for (String path : deleteList) {
                String[] arr = path.split("/");
                String name = arr[arr.length - 1];
                String pid = name.split("\\.")[0];
                if (alreadyLoadedData.contains(pid)) {
                    deleteFile(path);
                }
            }
        }
//        print(alreadyLoadedData.size());
//        for(String pid : alreadyLoadedData){
//            print(pid);
//        }

    }

    public static void loadVideosNewWay(String saveDir) throws Exception{
        List<String []> csvDataList = getCSVList("top_all.tsv");
        Set<String> alreadyLoadedData = getAlreadyData("top_hot_videos_30_60");
        int count = alreadyLoadedData.size();
        int max_num = -1;
        int count_num = 0;
        for(int i=0;i<csvDataList.size();i++){
            String[] row = csvDataList.get(i);
            List<String>list = parseRow(row,true);
            String pid = list.get(0);
            if(alreadyLoadedData.contains(pid)){
                count_num += 1;
                if(count_num == count){
                    max_num = i;
                    break;
                }
            }
        }
        print(max_num);
        Map<String,String>videosLenMap =  getTsvMap("videos_len.tsv");
        for(int i=max_num;i<csvDataList.size();i++){
            String[] row = csvDataList.get(i);
            List<String>list = parseRow(row,true);
            String pid = list.get(0);
            String readCount = list.get(2);
            String bucketName = list.get(5);
            String bucketKey = list.get(6);
            if(!alreadyLoadedData.contains(pid)){
                if(videosLenMap.containsKey(pid)){
                    float time_num = Float.valueOf(videosLenMap.get(pid));
                    if(time_num >= 1200 && time_num <= 4000) {
                        String type = bucketKey.split("\\.")[1];
                        String savePath = saveDir + "/" + pid + "."+readCount+ type;
                        try {
                            BaiduUtil.saveBucketContent(bucketName, bucketKey, savePath);
                        }catch(Exception e){
                            print(e);
                            continue;
                        }
                        count += 1;
                        print(readCount);
                        if(count > 200){
                            break;
                        }
                    }

                }
//                String type = bucketKey.split("\\.")[1];
//                String savePath = saveDir + "/" + pid + "."+readCount+ type;
//                try {
//                    BaiduUtil.saveBucketContent(bucketName, bucketKey, savePath);
//                }catch(Exception e){
//                    print(e);
//                    continue;
//                }
//                count += 1;
//                float time_num = Float.valueOf(count(savePath));
//                if(time_num < 1200 || time_num > 4000){
//                    deleteFile(savePath);
//                    count -= 1;
//                }
//                print(readCount);
//                if(count > 200){
//                    break;
//                }
            }
        }
    }


    public static void main(String[] args)throws Exception{
//        loadVideos("top_hot_videos_30_60");
        loadVideosNewWay("top_hot_videos_30_60");
//        deleteVideos();

    }
}