import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;


public class GetPixivImage {

	
	public static void main(String[] args){
		System.out.println("欢迎使用p站图片抓取工具\n"
				+ "tip：\n"
				+ "1：输入exit为退出程序\n"
				+ "2：输入从其他路径得到的p站id\n"
				+ "3：网络环境差可能导致图片下载失败\n"
				+ "4：默认保存路径为D盘下的pixiv目录\n");
		Scanner scanner = new Scanner(System.in);
		for(;;){
			System.out.println("请输入pixivid：");
			String pixivid = scanner.next();
			if("exit".equals(pixivid)){
				scanner.close();
				System.out.println("已退出");
				break;
				
			}
			try {
				String url = "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=";
				Map<String, String> map = getImgUrl(pixivid, url);
				String imgurl = map.get("imgurl");
				String title = map.get("title");
				InputStream is = getImgIO(imgurl, url);
				if(is == null) return;
				SimpleDateFormat format = new SimpleDateFormat("/yyyy/MM/dd/");
				String date = format.format(new Date());
				
				title = replaceAllStr(title);
				
				File filedir = new File("d:/pixiv/img"+date);
				if(!filedir.exists()){
					filedir.mkdirs();
				}
				File file = new File(filedir,title+".png");
				OutputStream os = new FileOutputStream(file);
				IOUtils.copy(is, os);
				os.close();
				is.close();
				System.out.println("下载完成");
			} catch (Exception e) {
				System.err.println("出现异常，请输入正确的p站图片id");
			}
			
		}
	}
	
	
	/**
	 * 获取图片输入流
	 * @param imgurl 图片地址
	 * @param Referer referer地址，跳转过来的地址，不传会导致403
	 * @return
	 * @throws Exception 
	 */
	public static InputStream getImgIO(String imgurl,String Referer) throws Exception{
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(imgurl);
		InputStream is = null;
		method.addRequestHeader("Referer", Referer);
		method.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
		client.executeMethod(method);
		//System.err.println(method.getResponseBodyAsStream());
		is = method.getResponseBodyAsStream();
		//method.releaseConnection();//释放连接
		
		return is;
	}
	
	
	
	
	/**
	 * 获取图片地址的方法
	 */
	public static Map<String,String> getImgUrl(String pixivid,String url) throws Exception{
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url+pixivid);
		method.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
		client.executeMethod(method);//执行请求
		Map<String,String> map = new HashMap<String, String>();
		if(method.getStatusCode() == 200){
			String html = method.getResponseBodyAsString();
			//释放连接
			method.releaseConnection();
			//获取html对象
			Document doc = Jsoup.parse(html);
			
			Element element = doc.select("div[class=img-container]").get(0).selectFirst("img");
			String img600url = element.attr("src");
			//将小图链接拼接成大图链接
			//String tmpStr = img600url.replaceFirst("c/600x600/img-master", "img-original");
			//String orignUrl = tmpStr.replaceFirst("_master1200", "");
			map.put("imgurl", img600url);
			map.put("title", element.attr("title"));
		}
		return map;
	}
	
	
	/**
	 * 替换掉所有不规则命名
	 * @param str
	 * @return
	 */
	public static String replaceAllStr(String str){
		if(str == null || "".equals(str)){
			str = UUID.randomUUID().toString();
		}
		if(str.contains("/")){
			str  = str.replaceAll("/", "");
		}
		if(str.contains("\"")){
			str  = str.replaceAll("\"", "");
		}
		if(str.contains(">")){
			str  = str.replaceAll(">", "");
		}
		if(str.contains("<")){
			str  = str.replaceAll("<", "");
		}
		if(str.contains("|")){
			str  = str.replaceAll("|", "");
		}
		if(str.contains("?")){
			str  = str.replaceAll("?", "");
		}
		if(str.contains(":")){
			str  = str.replaceAll(":", "");
		}
		if(str.contains("\\")){
			str  = str.replaceAll("\\", "");
		}

		if(str.contains("*")){
			str  = str.replaceAll("*", "");
		}
		return str;
	}
	
	
}
