package jpa_ex.shop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpa_ex.shop.domain.Address;
import jpa_ex.shop.domain.Member;
import jpa_ex.shop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    // member 등록

    /**
     * 1. 엔티티를 직접 받게 되면 엔티티를 변경 시 api 스펙 자체가 바뀌어버린다.
     * (엔티티는 바뀔 확률이 높아 엔티티를 직접 받지 않도록)
     * 2. 엔티티에 api 검증 로직이 들어간다...@NotEmpty...
     * ==> DTO를 만들어 DTO로 받는다. ( 검증, 스펙 변경 x )
     */
    @PostMapping("/api/v1/members")
    CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // member 수정

    @PutMapping("/api/v2/members/{id}")
    UpdateMemberResponse updateMemberV2(@PathVariable Long id,
                                        @RequestBody UpdateMemberRequest request) {
        memberService.update(id, request.getName());

        Member findMember = memberService.findOne(id);

        return new UpdateMemberResponse(id, findMember.getName());
    }

    // member 조회

    /**
     * 회원 정보만을 원했지만 응답 값으로 entity 를 직접 노출 ==> 필요없는 orders 까지 끌고옴
     * 이걸 막기 위해 필요 없는 필드에 @JsonIgnore 를 해줄 수도 있지만
     * 하나의 Entity 에 여러 API 를 위한 프레젠테이션 응답 로직 담기 불가
     * <p>
     * ==> API 응답 스펙에 맞게 별도의 DTO 만들어야함!
     */
    @GetMapping("/api/v1/members")
    List<Member> membersV1() {
        return memberService.findMembers();
    }

    /**
     * 새로운 DTO 를 사용하더라도 List<DTO> 를 바로 반환하면 후에 api 스펙 변경 시 용의하지 않다.
     * 고로 object 로 감싸서 응답!
     */
    @GetMapping("/api/v2/members")
    Result membersV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDTO> collect = findMembers.stream().map(m -> new MemberDTO(m.getName(), m.getAddress()))
                .collect(Collectors.toList());
        return new Result(findMembers.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDTO {
        private String name;
        private Address address;
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class CreateMemberResponse {
        private Long id;
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }
}
