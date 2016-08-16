package com.lib.service.user;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hankcs.hanlp.HanLP;
import com.lib.dto.JsonResult;
import com.lib.entity.FileInfo;
import com.lib.enums.Const;
import com.lib.utils.HtmlToWord;
import com.lib.utils.StringValueUtil;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.model.ConsolePageModelPipeline;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.pipeline.PageModelPipeline;

@TargetUrl("http://wenku.baidu.com/view/\\w+")
@HelpUrl("http://baike.baidu.com/wikitag/taglist?tagId=68031")
class test implements PageModelPipeline<WebMagicTest> {

	@Resource
	private FileInfoService fileInfoService;
	
	

	public test(FileInfoService fileInfoService) {
		super();
		this.fileInfoService = fileInfoService;
	}



	@Override
	public void process(WebMagicTest t, Task task) {
		// TODO Auto-generated method stub
		System.out.println(t.getTitle());
		System.out.println(t.getBrief());
		String uuid = StringValueUtil.getUUID();
		String path = "D:/soklib/" + "users/" + "2016001" + "/files/" + uuid + ".pdf";
		try {
			HtmlToWord.HtmlToPdf("<P>" + t.getBrief() + "</P>", path);
			File file = new File(path);
			
			FileInfo fi = new FileInfo();
			fi.setFileName(t.getTitle());
			fi.setFileSize(file.length());
			fi.setFileExt("pdf");
			fi.setFileBrief(HanLP.extractSummary(t.getBrief(), 3).toString());
			fi.setFileUserId(2016001L);
			fi.setFileUuid(uuid);
			fi.setFilePath("users/2016001/files/" + uuid);
			fi.setFileState(2);
			fi.setFileClassId(1l);
			System.out.println(fi);
			int res = fileInfoService.insertFile(fi);
			
			System.out.println(res);
			// 处理文件
			new Thread() {
				public void run() {
					try {
						
						fileInfoService.translateFile(uuid);
					} catch (Exception e) {
						// LOG.error(uuid + "文件处理失败");
					}
				};
			}.start();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

}

public class WebMagicTest {

	@ExtractBy(value = "//dd[@class='lemmaWgt-lemmaTitle-title']/h1/text()", notNull = true)
	private String title;

	@ExtractBy("//div[@class='lemma-summary']/allText()")
	private String brief;

	@Override
	public String toString() {

		return "WebMagicTest [title=" + title + ", brief=" + brief + "]";
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}



}
