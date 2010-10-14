package unicode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import unicode.exception.NotSupportedEncdoingException;
import unicode.utf8.UTF8Decoder;
import unicode.util.Utilities;


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
		
		File sourceFile = new File(source);

		if (!sourceFile.exists()) {
			
			System.out.println("not exist " + "\"" + args[0] + "\"");
			
			return;
			
		}
		
		BufferedInputStream bInputStream = null;
		BufferedOutputStream bOutputStream = null;
		
		try {
			
			bInputStream = new BufferedInputStream(new FileInputStream(sourceFile));
			long fileSize = sourceFile.length();
			byte[] buffer = new byte[(int) fileSize];
			int readedBytes = 0;
			
			
			// 끝까지 안전하기 읽기위해 한번 읽은 byte 개수를
			// 읽어야할 전체 byte 개수에서 감소시켜 나감
			// 얼마나 더 읽어들어야 하는지 확인
			// 읽어 들인 byte 가 0이거나 파일끝(-1)에 도달하면 종료 
			while (true) {

				readedBytes = bInputStream.read(buffer, readedBytes, (int) fileSize);

				if (0 >= readedBytes) {
					break;
				}
				
				fileSize -= readedBytes;
				
			}
			
			// 읽어들인 파일 byte data 를 string object 로 1차 UTF-8로 변환하고
			// UTF-8을 decoding 해서 2차 결과로 UNICODE code point 로 변환한다
			// 문자 하나당 최대 4byte, 때문에 integer 변수하나에 문자 하나씩 저장
			String text = new String(buffer, sourceCharset);
			byte[] textBytes = text.getBytes("UTF-8");
			Decoder decoder = new UTF8Decoder();
			ArrayList<Integer> unicode = decoder.decode(textBytes);
			
			// 입력받은 목표 encoding 으로 적합한 Encoder object 를 생성한다
			// 목표 encoding 을 지원하지 않는다면 예외발생
			Encoder encoder = null;
			
			try {
				encoder = EncoderFactory.getEncoder(destCharset);
			} catch (NotSupportedEncdoingException e) {
				e.printStackTrace();
			}
			
			// 최종 결과를 담을 영역
			// byte order mark 를 위해 4byte 추가
			byte[] finalByteData = new byte[unicode.size() * 4 + 4];
			
			// encoding 시작
			// UNICODE code value, 변환된 data 를 저장할 buffer 공간
			// numberTotalBytes 는 encoding 과정에서 생긴 byte 개수
			// buffer 공간을 최대치로 잡았기 때문에
			// 어디까지 유효한 data 인지 알아야 한다
			int numberTotalBytes = encoder.encode(unicode, finalByteData);
			
			// 목표파일이름 만들기
			String destFileName = sourceFile.getPath().substring(0, sourceFile.getPath().lastIndexOf('.')) +  "_" + destCharset + ".txt";
			
			// 존재여부 검사 후 중복되지 않는 이름으로 File object 생성
			File destFile = Utilities.generateFilePathAndName(destFileName);
			
			bOutputStream = new BufferedOutputStream(new FileOutputStream(destFile));
			bOutputStream.write(finalByteData, 0, numberTotalBytes);
			bOutputStream.flush();
			
			System.out.println("exported:");
			System.out.println(destFile.getPath());
			
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