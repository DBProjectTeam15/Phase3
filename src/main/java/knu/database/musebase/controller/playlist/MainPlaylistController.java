package knu.database.musebase.controller.playlist;

import knu.database.musebase.auth.SessionWrapper;
import knu.database.musebase.console.PageKey;
import knu.database.musebase.service.PlaylistService;


/**
 * 이전 상태값을 저장해야하는데, 시간이 없어서... 추가적으로 구현했습니다.
 * <br/>
 * TODO: 이전 상태로 돌아가는 명령을 할 시, 전역 상태에서 가져오기
 */
public class MainPlaylistController extends PlaylistController {

    public MainPlaylistController(PlaylistService playlistService) {
        super(playlistService);
    }


    @Override
    public PageKey invoke(String[] commands){
        if  (commands[0].equals("0")) return PageKey.MAIN;
        else return PageKey.PLAYLIST_PAGE;
    }
}
