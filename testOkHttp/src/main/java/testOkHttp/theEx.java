package testOkHttp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class theEx {

	public static List<String> urlLoop = new ArrayList<String>();
	public static List<String> imgLoop = new ArrayList<String>();
	public static String mainUrl;
	
	
	public static String getHtml(String url) throws IOException {
		if(theEx.urlLoop.contains(url))
			return null;
		else
			theEx.urlLoop.add(url);
		
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(url).get()
				.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36").build();
				Response response = null;
		try {
			response = client.newCall(request).execute();
			if(response.isSuccessful())
				System.out.println("got html");
			else {
				System.out.println("responseFail");
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response.body().string();
	}
	
	
	
	public static void downloader(String imgUrl) throws IOException  {
		if(theEx.imgLoop.contains(imgUrl))
			return;
		else
			theEx.imgLoop.add(imgUrl);
		OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(5, TimeUnit.SECONDS).build();
		
		Request req = new Request.Builder().url(imgUrl).get()
				.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36").build();
		Response res;
		try {
			res = client.newCall(req).execute();
		} catch (IOException e) {
			System.out.println(imgUrl+"链接已超时,略过此图");
			return;
		}
		
		String fileName = null;
		InputStream is =  res.body().byteStream();
		FileOutputStream fos = null;
		byte[] buffer = new byte[10000];
		File file = new File((fileName = UUID.randomUUID().toString())+".jpg");
		if(!file.exists()) {
			file.createNewFile();
			fos = new FileOutputStream(file);
			int len = 0;
			while((len=is.read(buffer))!=-1) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
			fos.close();
			is.close();
			System.out.println(fileName + "已下载完成");
		}
	}
	
	public static void getHref(String html) throws IOException {
		if(html==null)
			return;
		Document doc = Jsoup.parse(html);
		Elements imgEles = doc.select("img[src$=.jpg]");
		Iterator<Element> imgEli = imgEles.iterator();
		while(imgEli.hasNext()) {
			Element imgEl = imgEli.next();
			String imgUrl = imgEl.attr("src");
			if(imgUrl.length()<8||(!imgUrl.substring(0, 8).equals("https://"))&&!imgUrl.substring(0, 7).equals("http://"))
				imgUrl = theEx.mainUrl + imgUrl ;
			System.out.println(imgUrl);
			theEx.downloader(imgUrl);
		}
		Elements eles = doc.select("a[href]");
		Iterator<Element> eli = eles.iterator();
		while(eli.hasNext()) {
			Element el = eli.next();
			String url = el.attr("href");
			if(url.length()<8||(!url.substring(0, 8).equals("https://"))&&!url.substring(0, 7).equals("http://"))
				url = theEx.mainUrl + url ;
			System.out.println("现已进入更深一层,地址为"+url);
			theEx.getHref(theEx.getHtml(url));
		}
		System.out.println("返回一层");
	}
	
	public static void main(String[] args) {
		theEx.mainUrl = "http://www.xfyy970.com";
		try {
			theEx.getHref(theEx.getHtml(theEx.mainUrl));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
