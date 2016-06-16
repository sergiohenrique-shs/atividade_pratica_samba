package br.com.shs.atividadepratica.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;


@WebServlet(urlPatterns = {"/upload"}, description = "Servlet que recebe o arquivo de video a ser convertido.")
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
                 maxFileSize=1024*1024*10,      // 10MB
                 maxRequestSize=1024*1024*50)   // 50MB
public class FileUploaderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String BOUNDARY_STRING = "boundary=";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileUploaderServlet() {
        super();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
		
		int posicaoBoundary = cabecalho.indexOf("boundary=");
		
		String str = cabecalho.substring(posicaoBoundary+BOUNDARY_STRING.length());
		
		//String boundary = cabecalho.indexOf("boundary=");
		
		InputStream is = request.getInputStream();
		File file = new File("C:\\Documents and Settings\\sergio\\Desktop\\conteudo_post.txt");
		FileOutputStream out = null;
		byte[] buffer = new byte[1024*4];
		int qtdBytes = 0;
		
		try {
			while((qtdBytes = is.read(buffer)) != -1){
				
				if(out == null){
					out = new FileOutputStream(file);
				}
				
				out.write(buffer, 0, qtdBytes);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(is != null){
				is.close();
			}
			
			if(out != null){
				out.close();
			}
			
			//file.delete();
		}
		
		response.sendRedirect("/");
	}
}
