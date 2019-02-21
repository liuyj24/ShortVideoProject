package com.shenghao.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MergeVideoMp3 {

    private String ffmpegEXE;

    public MergeVideoMp3(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    /**
     * 转换视频的方法
     */
    public void convertor(String videoInputPath, String mp3InputPath, double seconds, String videoOutputPath) throws IOException {
        //ffmpeg.exe -i 等待合并的视频.mp4 -i bgm.mp3 -t 10 -y 新的视频.mp4
        List<String> command = new ArrayList<>();

        //拼接命令, ProcessBuilder会自动给出空格
        command.add(ffmpegEXE);

        command.add("-i");
        command.add(videoInputPath);

        command.add("-i");
        command.add(mp3InputPath);

        command.add("-t");
        command.add(String.valueOf(seconds));

        command.add("-y");
        command.add(videoOutputPath);

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
        MergeVideoMp3 ffmpeg = new MergeVideoMp3("C:/ffmpeg/bin/ffmpeg.exe");
        try {
            ffmpeg.convertor("C:/ffmpeg/bin/无用视频.mp4",
                    "C:\\ffmpeg\\bin\\song1.mp3", 11, "C:\\ffmpeg\\bin\\合成后的视频.mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
