package unicode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import unicode.exception.NotSupportedCharsetException;
import unicode.utf8.UTF8Decoder;


public class TransformerMain {

	public static void main (String[] args) {
		
		if (args.length < 3) {
			
			System.out.println("not enough arguments");
			System.out.println("ex)sourcefile.txt euc-kr(source file charset) utf-16le(dest file charset)");
			
			return;
			
		}
		
		String source = args[0];
		String sourceCharset = args[1];
		String destCharset = args[2];
		
		URL sourcePath = TransformerMain.class.getResource(source);
		

		if (null == sourcePath) {
			
			System.out.println("not exist " + "\"" + args[0] + "\"");
			return;
			
		}
		
		File sourceFile = new File(sourcePath.getFile());
		
		BufferedInputStream bInputStream = null;
		BufferedOutputStream bOutputStream = null;
		try {
			
			bInputStream = new BufferedInputStream(new FileInputStream(sourceFile));
			byte[] buffer = new byte[(int) sourceFile.length()];
			int readedBytes =  0 ;
			long fileSize = sourceFile.length();
			
			// 끝까지 안전하기 읽기위해 한번 읽은 byte 개수를
			// 총 byte 개수에서 감소시켜 나감
			// 얼마나 더 읽어들어야 하는지 확인
			// 읽어 들인 byte 가 0이거나 파일끝(-1)에 도달하면 종료 
			while (true) {
				
				readedBytes = bInputStream.read(buffer, readedBytes, (int) (fileSize - readedBytes));
				
				if (1 < readedBytes) {
					break;
				}
				
			}
						
			// 읽어들인 파일 byte data 를 string object 로 1차 UTF-8로 변환하고
			// UTF-8을 decoding 해서 2차 결과로 UNICODE character code 로 변환한다
			// 문자 하나당 최대 4byte, 때문에 integer 변수하나에 문자 하나씩 저장
			String text = new String(buffer, sourceCharset);
			byte[] textBytes = text.getBytes("UTF-8");
			UTF8Decoder decoder = new UTF8Decoder();
			ArrayList<Integer> unicode = decoder.decode(textBytes, textBytes.length);
			
			// 입력받은 encoding encoding 방식을 지정하고
			// 적합한 Encoder object 를 생성한다
			// 목적 encoding 을 지원하지 않는다면 예외발생
			Encoder encoder = null;
			
			try {
				
				encoder = EncoderFactory.getEncoderFactory(destCharset);
				
			} catch (NotSupportedCharsetException e) {
			
				e.printStackTrace();
				
			}
			
			// 최종 결과를 담을 영역
			// byte order mark 가 있는 UTF-8 일 경우를 위해 3byte 추가
			byte[] finalData = new byte[unicode.size() * 4 + 3];
			
			// encoding 시작
			// UNICODE code value 와 변환된 data 를 저장할 buffer 공간
			// UNICODE code value 를 encoding 에 맞게 변환된 byte 수
			// buffer 공간을 최대치로 잡았기 때문에
			// 어디가 data 의 끝인지 알아야 하기 때문에
			int usedBytes = encoder.encode(unicode, finalData);
			
			bOutputStream = new BufferedOutputStream(new FileOutputStream(sourceFile.getPath().substring(0, sourceFile.getPath().lastIndexOf('.')) +  "_" + destCharset + ".txt"));
			
			bOutputStream.write(finalData, 0, usedBytes);
			bOutputStream.flush();
			
			System.out.println(sourceFile.getPath().substring(0, sourceFile.getPath().lastIndexOf('.')) +  "_" + destCharset + ".txt");
			
		} catch (IOException ie) {
			
			ie.printStackTrace();
			
		} finally {
			
			try {
				
				if (bInputStream != null) {
					bInputStream.close();
				}
				
				if (bOutputStream != null) {
					bOutputStream.close();
				}
				
			} catch (IOException e) {
				
				e.printStackTrace();
				
			}
			
		}
		
	}

}