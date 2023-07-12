
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.vbj_fragments.MainActivity
import com.example.vbj_fragments.R
import java.util.*

abstract class BaseFragment : Fragment() {

    abstract fun getLayoutId(): Int
    abstract fun getQuestion(): String
    private var textToSpeech: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var speechRecognizerIntent: Intent? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.nextButton).setOnClickListener {
            (activity as? MainActivity)?.showNext()
        }
        view.findViewById<ImageButton>(R.id.voiceBtn)?.setOnClickListener {
            // Start listening only after TTS has finished speaking
            if (textToSpeech?.isSpeaking == false) {
                listenToSpeechInput()
            }
        }

    }

    private fun listenToSpeechInput() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {}

            override fun onResults(results: Bundle) {
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null)
                    view?.findViewById<EditText>(R.id.editText)?.setText(matches[0]) // Set first match to EditText
            }

            override fun onPartialResults(partialResults: Bundle) {}
            override fun onEvent(eventType: Int, params: Bundle) {}
        })

        speechRecognizer?.startListening(speechRecognizerIntent)
    }

    override fun onResume() {
        super.onResume()
        textToSpeech = TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech?.language = Locale.UK
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        // Disable voiceBtn until speech finishes
                        view?.findViewById<ImageButton>(R.id.voiceBtn)?.isEnabled = false
                    }

                    override fun onDone(utteranceId: String?) {
                        // Enable voiceBtn after speech finishes
                        view?.findViewById<ImageButton>(R.id.voiceBtn)?.isEnabled = true
                    }

                    override fun onError(utteranceId: String?) {
                    }
                })
                val params = Bundle()
                textToSpeech?.speak(getQuestion(), TextToSpeech.QUEUE_FLUSH, params, "UtteranceId")
            }
        }
        // Check and request permissions at runtime
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_REQUEST_CODE)
        }
    }



    override fun onPause() {
        super.onPause()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        speechRecognizer?.stopListening()
        Toast.makeText(context, "Fragment paused", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        speechRecognizer?.destroy()
    }

    companion object {
        private const val RECORD_AUDIO_REQUEST_CODE = 1
    }
}
