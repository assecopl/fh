package pl.fhframework.model;

import lombok.Getter;

public enum KeyboardEventShortcutEnum {
    CTRL_A("CTRL+A"),
    CTRL_B("CTRL+B"),
    CTRL_C("CTRL+C"),
    CTRL_D("CTRL+D"),
    CTRL_E("CTRL+E"),
    CTRL_F("CTRL+F"),
    CTRL_G("CTRL+G"),
    CTRL_H("CTRL+H"),
    CTRL_I("CTRL+I"),
    CTRL_J("CTRL+J"),
    CTRL_K("CTRL+K"),
    CTRL_L("CTRL+L"),
    CTRL_M("CTRL+M"),
    CTRL_N("CTRL+N"),
    CTRL_O("CTRL+O"),
    CTRL_P("CTRL+P"),
    CTRL_Q("CTRL+Q"),
    CTRL_R("CTRL+R"),
    CTRL_S("CTRL+S"),
    CTRL_T("CTRL+T"),
    CTRL_U("CTRL+U"),
    CTRL_V("CTRL+V"),
    CTRL_W("CTRL+W"),
    CTRL_X("CTRL+X"),
    CTRL_Y("CTRL+Y"),
    CTRL_Z("CTRL+Z"),
    ENTER("ENTER"),
    ESC("ESC"),
    F1("F1"),
    F2("F2"),
    F3("F3"),
    F4("F4"),
    F5("F5"),
    F6("F6"),
    F7("F7"),
    F8("F8"),
    F9("F9"),
    F10("F10"),
    F11("F11"),
    F12("F12");

    KeyboardEventShortcutEnum(String shortcuts){
        this.shortcuts = shortcuts;
    }

    @Getter
    private String shortcuts;
}
