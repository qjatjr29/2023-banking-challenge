package numble.banking.support;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;

public interface ApiDocumentUtils {

  static OperationRequestPreprocessor getDocumentRequest() {
    return preprocessRequest(
        modifyUris() // 문서상의 uri 의 기본값을 http://localhost:8080 => https://docs.api.com 로 변경
            .scheme("https")
            .host("docs.api.com")
            .removePort(),
        prettyPrint()); // 문서의 request 을 이쁘게 출력하기 위함
  }

  static OperationResponsePreprocessor getDocumentResponse() {
    return preprocessResponse(prettyPrint()); // 문서의 response 을 이쁘게 출력하기 위함
  }

}
