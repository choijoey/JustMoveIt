package com.ssafy.CommonPJT.service;

import com.ssafy.CommonPJT.domain.MoviePlayingInfo;
import com.ssafy.CommonPJT.domain.Ticket;
import com.ssafy.CommonPJT.dto.Ticket.TicketResDto;
import com.ssafy.CommonPJT.dto.Ticket.TicketSaveDto;
import com.ssafy.CommonPJT.repository.MoviePlayingInfoRepository;
import com.ssafy.CommonPJT.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final MoviePlayingInfoRepository moviePlayingInfoRepository;

    // 티켓 추가
    @Transactional
    public void save(TicketSaveDto requestDto) {
        Long moviePlayingInfoId = requestDto.getMoviePlayingInfoId();
        MoviePlayingInfo info = moviePlayingInfoRepository.findById(moviePlayingInfoId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 값입니다."));
        Ticket toEntity = requestDto.toEntity(info);
        ticketRepository.save(toEntity);
    }

    // 티켓 리스트 조회
    public List<TicketResDto> findTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        return tickets.stream().map(TicketResDto::new).collect(Collectors.toList());
    }

    // 특정 티켓 조회
    public List<TicketResDto> findByNum(String phoneNumber) {
        List<Ticket> tickets = ticketRepository.findAll();
        List<Ticket> ticketsByNum = new ArrayList<>();
        for (Ticket ticket : tickets) {
            if (ticket.getPhoneNumber().equals(phoneNumber)) {
                ticketsByNum.add(ticket);
            }
        }
        return ticketsByNum.stream().map(TicketResDto::new).collect(Collectors.toList());
    }

    // ticketId로 티켓 조회
    public TicketResDto findById(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티켓입니다."));
        return new TicketResDto(ticket);
    }

    // 티켓 예매 취소
    @Transactional
    public void delete(Long id) {
        Ticket target = ticketRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("잘못된 티켓번호입니다."));
        ticketRepository.delete(target);
    }
}
