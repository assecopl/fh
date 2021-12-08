package pl.fhframework.dp.commons.fh.declaration.handling;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.fhframework.dp.transport.dto.alerts.AlertDto;
import pl.fhframework.model.forms.PageModel;

import java.util.List;

public class DeclarationAlertPagedTableSource {

        List<AlertDto> alertDtoList;

        public DeclarationAlertPagedTableSource(List<AlertDto> elements) {
            this.alertDtoList = elements;
        }

        public PageModel<AlertDto> createPagedModel() {
            return new PageModel<>(pageable -> loadRegisterHFPage(pageable));
        }

        private Page<AlertDto> loadRegisterHFPage(Pageable pageable) {
            int start = Math.toIntExact(pageable.getOffset());
            int end = Math.min((start + pageable.getPageSize()), alertDtoList.size());
            return new PageImpl<>(alertDtoList.subList(start, end), pageable, alertDtoList.size());
        }
}
