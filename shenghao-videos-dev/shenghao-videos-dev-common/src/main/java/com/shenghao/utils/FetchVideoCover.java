package com.shenghao.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FetchVideoCover {

    private String ffmpegEXE;

    public FetchVideoCover(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    /**
     * 转换视频的方法
     */
    public void convertor(String videoInputPath, String coverOutputPath) throws IOException {
        //ffmpeg.exe -ss 00:00:01 -y -i xxx.mp4 -vframes 1 new.jpg
        List<String> command = new ArrayList<>();

        //拼接命令, ProcessBuilder会自动给出空格
        command.add(ffmpegEXE);

        //指定截取第一秒
        command.add("-ss");
        command.add("00:00:01");

        command.add("-y");
        command.add("-i");
        command.add(videoInputPath);

        command.add("-vframes");
        command.add("1");

        command.add(coverOutputPath);


        ProcessBuilder builder = new ProcessBuilder(command);//ProcessBuilder是一个执行命令的类
        Process process = builder.start();//异常抛出去,在调用处try-catch

        InputStream errorStream = process.getErrorStream();//ErrorStream其实就是inputStream, 对他进行读取, 读取就是释放.
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);//封装好后, 对br进行处理即可

        String line = "";
        while((line = br.readLine()) != null){
        }
        if (br != null){
            br.close();
        }
        if (inputStreamReader != null){
            inputStreamReader.close();
        }
        if (errorStream != null){
            errorStream.close();
        }
    }

    public static void main(String[] args){
        FetchVideoCover ffmpeg = new FetchVideoCover("C:/ffmpeg/bin/ffmpeg.exe");
        try {
            ffmpeg.convertor("C:\\ffmpeg\\bin\\video.mp4", "C:\\ffmpeg\\bin\\video.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
