package com.speechpro.onepass.framework;

import android.content.Context;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by alexander on 13.09.17.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P, packageName = "com.speechpro.onepass.framework")
public class ResourceTest {

    private Context context;

    @Before
    public void setUp() throws Exception {
        context = RuntimeEnvironment.application;
    }

    @Test
    @Config(qualifiers="ru")
    public void shouldUseRussianResourcesStrings() {
        assertThat(context.getString(R.string.network_error)).isEqualTo("Сбой подключения, повторите попытку позже");
        assertThat(context.getString(R.string.again)).isEqualTo("Пожалуйста, попробуйте еще раз");
        assertThat(context.getString(R.string.again_button)).isEqualTo("ПОВТОРИТЬ");
        assertThat(context.getString(R.string.enrolled)).isEqualTo("Пользователь уже зарегистрирован");
        assertThat(context.getString(R.string.internal_server_error)).isEqualTo("Внутренняя ошибка сервера");
        assertThat(context.getString(R.string.languages_do_not_match)).isEqualTo("Язык не соответствует языку регистрации");
        assertThat(context.getString(R.string.not_enrolled)).isEqualTo("Пользователь не зарегистрирован");
        assertThat(context.getString(R.string.please_position_your_face_into_the_mask)).isEqualTo("Пожалуйста, совместите лицо с маской");
        assertThat(context.getString(R.string.pronounce)).isEqualTo("Произнесите цифры:");
        assertThat(context.getString(R.string.quiet)).isEqualTo("Удостоверьтесь в тишине");
        assertThat(context.getString(R.string.retry)).isEqualTo("ПОВТОРИТЬ");
        assertThat(context.getString(R.string.toast_incorrect_pronunciation)).isEqualTo("Неверное произношение");
        assertThat(context.getString(R.string.toast_server_error)).isEqualTo("Что-то пошло не так");
        assertThat(context.getString(R.string.toast_unknown_error)).isEqualTo("Неизвестная ошибка, повторите попытку позже.");
        assertThat(context.getString(R.string.voice_title)).isEqualTo("Нажмите кнопку и произнесите следующие цифры:");
        assertThat(context.getString(R.string.give_it_another_shot)).isEqualTo("Попробуйте сделать другой снимок");
        assertThat(context.getString(R.string.give_it_another_recording)).isEqualTo("Попробуйте сделать другую запись");
        assertThat(context.getString(R.string.give_it_another_video)).isEqualTo("Попробуйте сделать другое видео");
        assertThat(context.getString(R.string.done)).isEqualTo("ГОТОВО");
        assertThat(context.getString(R.string.sign_up_complete)).isEqualTo("Регистрация завершена");
        assertThat(context.getString(R.string.you_can_now_signin)).isEqualTo("Теперь Вы можете войти в свой аккаунт");
        assertThat(context.getString(R.string.camera_locked)).isEqualTo("Камера заблокирована");
        assertThat(context.getString(R.string.go_to_settings)).isEqualTo("Пожалуйста, в настройках приложения дайте необходимые разрешения.");
        assertThat(context.getString(R.string.look_at_the_camera)).isEqualTo("Посмотрите в камеру и нажмите кнопку записи");
    }

    @Test
    @Config(qualifiers="en")
    public void shouldUseEnglishResourcesStrings() {
        assertThat(context.getString(R.string.enroll_phrases_1)).isEqualTo("0123456789");
        assertThat(context.getString(R.string.enroll_phrases_2)).isEqualTo("9876543210");
        assertThat(context.getString(R.string.episode1)).isEqualTo("1");
        assertThat(context.getString(R.string.episode2)).isEqualTo("2");
        assertThat(context.getString(R.string.episode3)).isEqualTo("3");
        assertThat(context.getString(R.string.voice_title)).isEqualTo("Press the button and pronounce following digits:");
        assertThat(context.getString(R.string.quiet)).isEqualTo("Make sure it\'s quiet");
        assertThat(context.getString(R.string.again)).isEqualTo("Please try again");
        assertThat(context.getString(R.string.again_button)).isEqualTo("TRY AGAIN");
        assertThat(context.getString(R.string.pronounce)).isEqualTo("Pronounce the digits:");
        assertThat(context.getString(R.string.please_position_your_face_into_the_mask)).isEqualTo("Please position your face into the mask");
        assertThat(context.getString(R.string.retry)).isEqualTo("TRY AGAIN");
        assertThat(context.getString(R.string.internal_server_error)).isEqualTo("internal server error");
        assertThat(context.getString(R.string.languages_do_not_match)).isEqualTo("Languages do not match");
        assertThat(context.getString(R.string.give_it_another_shot)).isEqualTo("Give it another shot");
        assertThat(context.getString(R.string.give_it_another_recording)).isEqualTo("Give it another recording");
        assertThat(context.getString(R.string.give_it_another_video)).isEqualTo("Give it another video");
        assertThat(context.getString(R.string.done)).isEqualTo("DONE");
        assertThat(context.getString(R.string.sign_up_complete)).isEqualTo("Sign up complete");
        assertThat(context.getString(R.string.you_can_now_signin)).isEqualTo("You can now sign in to your account");
        assertThat(context.getString(R.string.camera_locked)).isEqualTo("Camera locked");
        assertThat(context.getString(R.string.ok)).isEqualTo("ok");
        assertThat(context.getString(R.string.title_agreement1)).isEqualTo("By proceeding you agree to process your biometric data");
        assertThat(context.getString(R.string.text_agreement_info1)).isEqualTo("We will take a photo and record three samples of your voice");
        assertThat(context.getString(R.string.option_info1)).isEqualTo("Find a well lit place");
        assertThat(context.getString(R.string.option_info2)).isEqualTo("Make sure it\'s quiet");
        assertThat(context.getString(R.string.option_info3)).isEqualTo("Take off sunglasses");
        assertThat(context.getString(R.string.option_info4)).isEqualTo("Make your ordinary face");
        assertThat(context.getString(R.string.button_agree)).isEqualTo("Continue");
        assertThat(context.getString(R.string.button_disagree)).isEqualTo("Disagree");
        assertThat(context.getString(R.string.go_to_settings)).isEqualTo("Go to settings to allow necessary permissions.");
        assertThat(context.getString(R.string.look_at_the_camera)).isEqualTo("Look at the camera and press the record button");
    }

    @Test
    @Config(qualifiers="ru")
    public void shouldUseRussianResourcesArrays() {
        assertThat(Arrays.toString(context.getResources().getStringArray(R.array.numbers)))
                .isEqualTo("[ноль, один, два, три, четыре, пять, шесть, семь, восемь, девять]");
    }

    @Test
    @Config(qualifiers="en")
    public void shouldUseEnglishResourcesArrays() {
        assertThat(Arrays.toString(context.getResources().getStringArray(R.array.numbers)))
                .isEqualTo("[zero, one, two, three, four, five, six, seven, eight, nine]");
    }
}
