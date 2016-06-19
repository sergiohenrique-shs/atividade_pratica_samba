package br.com.shs.atividadepratica.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;


@WebServlet(urlPatterns = {"/upload"}, description = "Servlet que recebe o arquivo de video a ser convertido.")
@MultipartConfig/*(fileSizeThreshold=1024*1024*2, // 2MB
                 maxFileSize=1024*1024*10,      // 10MB
                 maxRequestSize=1024*1024*50)   // 50MB*/
public class FileUploaderServlet extends HttpServlet {
	private static final String BUCKET_NAME = "testesamba";
	private static final long serialVersionUID = 1L;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileUploaderServlet() {
        super();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	Writer w = resp.getWriter();
    	w.write("Oi, "+new Date());
    	w.flush();
    	w.close();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//multipart/form-data; boundary=---------------------------1384145104390
		
		String cabecalho = request.getHeader("Content-Type");

		if(!cabecalho.contains("multipart/form-data;")){
			response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
			return;
		}
		
		Enumeration<String> headers = request.getHeaderNames();
		
		while(headers.hasMoreElements()){
			String nome = headers.nextElement();
			System.out.println(nome + "=" + request.getHeader(nome));
		}
		
		Part uploadedVideo = request.getPart("videoParaConverter");
		
		if(uploadedVideo != null){
			
			if(!uploadedVideo.getContentType().startsWith("video/")){
				response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
				return;
			}
			
			String nomeOriginal = uploadedVideo.getHeader("Content-Disposition").replaceFirst(".*filename=\"([^\"]+)\".*", "$1");
			InputStream is = uploadedVideo.getInputStream();
			File file = File.createTempFile(nomeOriginal, "");
			
			FileOutputStream out = null;
			byte[] buffer = new byte[1024*64];
			int qtdBytes = 0;
			
			try {
				while((qtdBytes = is.read(buffer)) != -1){
					
					if(out == null){
						out = new FileOutputStream(file);
					}
					
					out.write(buffer, 0, qtdBytes);
				}
				
				AmazonS3 s3 = new AmazonS3Client();
		        Region usWest2 = Region.getRegion(Regions.SA_EAST_1);
		        s3.setRegion(usWest2);
		        
		        PutObjectRequest putObjRequest = new PutObjectRequest(BUCKET_NAME, sdf.format(new Date()) + nomeOriginal, file);
		        
		        ObjectMetadata metadata = new ObjectMetadata();
		        metadata.setContentType(uploadedVideo.getContentType());
		        
		        putObjRequest.setMetadata(metadata);
		        
		        s3.putObject(putObjRequest);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(is != null){
					is.close();
				}
				
				if(out != null){
					out.close();
				}
			}
		}
		
		response.sendRedirect(request.getServletContext().getContextPath());
	}
}
