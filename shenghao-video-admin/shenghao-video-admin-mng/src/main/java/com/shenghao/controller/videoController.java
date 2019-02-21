package com.shenghao.controller;

import com.shenghao.enums.VideoStatusEnum;
import com.shenghao.pojo.Bgm;
import com.shenghao.pojo.Users;
import com.shenghao.service.UsersService;
import com.shenghao.service.VideoService;
import com.shenghao.utils.IMoocJSONResult;
import com.shenghao.utils.PagedResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.PageRanges;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/video")
public class videoController {
	
	@Autowired
	private UsersService usersService;

	@Autowired
	private VideoService videoService;

	@Value("${FILE_SPACE}")
	private String FILE_SPACE;
	
	@GetMapping("/showAddBgm")
	public String showList() {
		return "video/addBgm";
	}

	@GetMapping("/showBgmList")
	public String showBgmList() {
		return "video/bgmList";
	}

	/**
	 * 添加bgm
	 * @param bgm
	 * @return
	 */
	@PostMapping("/addBgm")
	@ResponseBody
	public IMoocJSONResult addBgm(Bgm bgm) {

		videoService.addBgm(bgm);
		return IMoocJSONResult.ok();
	}

	/**
	 * 查询bgm列表, 默认查询10页
	 * @param page
	 * @return
	 */
	@PostMapping("/queryBgmList")
	@ResponseBody
	public PagedResult queryBgmList(Integer page) {
		return videoService.queryBgmList(page, 10);
	}

	/**
	 * 删除bgm的接口
	 * @param bgmId
	 * @return
	 */
	@PostMapping("/delBgm")
	@ResponseBody
	public IMoocJSONResult delBgm(String bgmId) {
		videoService.deleteBgm(bgmId);
		return IMoocJSONResult.ok();
	}

	/**
	 * 显示举报视频列表
	 * @return
	 */
	@GetMapping("/showReportList")
	public String showReportList() {
		return "video/reportList";
	}

	@PostMapping("/reportList")
	@ResponseBody
	public PagedResult reportList(Integer page){
		PagedResult result = videoService.queryReportList(page, 10);
		return result;
	}

	@PostMapping("/forbidVideo")
	@ResponseBody
	public IMoocJSONResult forbidVideo(String videoId){
		videoService.updateVideoStatus(videoId, VideoStatusEnum.FORBID.value);
		return IMoocJSONResult.ok();
	}

	/**
	 * 上传bgm
	 * @param files
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/bgmUpload")
	@ResponseBody//返回json对象
	public IMoocJSONResult uploadFace(@RequestParam("file") MultipartFile[] files) throws Exception {//给上传的文件定义变量名file

		//文件保存的命名空间
//		String fileSpace = File.separator + "workspace_wxxcx" + File.separator + "shenghao_videos_dev"
//				+ File.separator + "mvc-bgm";
//		String fileSpace = "C:" + File.separator + "workspace_wxxcx" + File.separator + "shenghao_videos_dev"
//				+ File.separator + "mvc-bgm";


		//保存到数据库中的相对路径
		String uploadPathDB = File.separator + "bgm";

		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;

		try {
			if (files != null && files.length > 0){
				//先拿到用户上传的文件名, 并判断文件的名字是否为空
				String fileName = files[0].getOriginalFilename();
				if (StringUtils.isNotBlank(fileName)){
					//拼接文件的最终保存路径, 绝对路径
					String finalPath = FILE_SPACE + uploadPathDB + File.separator + fileName;
					//设置数据库保存的路径
					uploadPathDB += (File.separator + fileName);

					//为新文件创建文件夹, 如果已经有就不创建了
					File outFile = new File(finalPath);//在磁盘上创建文件对象, 这个时候文件是空的
					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()){
						//创建父文件夹
						outFile.getParentFile().mkdirs();
					}
					//把用户上传的文件拷贝到准备好的文件对象中
					fileOutputStream = new FileOutputStream(outFile);
					inputStream = files[0].getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
				}
			}else{
				return IMoocJSONResult.errorMsg("上传出错...");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return IMoocJSONResult.errorMsg("上传出错...");
		}finally{
			if (fileOutputStream != null){
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		}
		return IMoocJSONResult.ok(uploadPathDB);
	}
}
