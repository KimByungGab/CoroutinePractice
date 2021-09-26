package com.example.coroutineproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.coroutineproject.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.lang.Runnable

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        setListener(activityMainBinding)
    }

    private fun setListener(activityMainBinding: ActivityMainBinding) {
        activityMainBinding.button.setOnClickListener {
            testThreadAndCoroutine()
        }
    }

    // 코루틴은 쓰레드와 기능적으로는 같지만 쓰레드에 비하면 좀 더 가볍다.
    private fun testThreadAndCoroutine() {

// 1. 코루틴 기본 사용법
//=================================================================
        // 아래의 쓰레드와 코루틴은 똑같은 기능이 작동된다.
//        Thread(Runnable {
//            for(i in 1..10) {
//                Thread.sleep(1000)
//                Log.i("쓰레드", "쓰레드 동작중")
//            }
//        }).start()

        // 스코프는 총 3개가 있다.
        // GlobalScope : 앱의 생명주기와 함께 동작. 별도의 생명주기 관리가 필요없음. 앱이 계속 실행될 때 필요한 친구가 아니면 사용하지 않을 것 같다.
        // CoroutineScope : 커스텀한 코루틴. 필요할 때만 열고 닫아줄 때 사용. 아마 제일 많이 사용할 것 같다.
        // ViewModelScope : Jetpack 아키텍쳐의 뷰모델 컴포넌트 사용시 ViewModel 인스턴스에서 사용하기 위해 제공되는 스코프. 뷰모델 인스턴스가 소멸 시 자동으로 취소. 얘는.. 글쎄 사용할까? 아직 모르겠다.

        // GlobalScope 사용방법
//        GlobalScope.launch {
//            repeat(10) {
//                delay(1000)
//                Log.i("코루틴", "GlobalScope 코루틴 동작중")
//            }
//        }
//=================================================================

// 2. 코루틴스코프의 디스패쳐 종류와 코루틴 정지방법.
//=================================================================
        // CoroutineScope 사용방법
        // CoroutineScope를 사용하게되면 무조건 디스패쳐라는 것을 인자값으로 넣어야된다.(그렇다고 GlobalScope는 디스패쳐 못 넣는 건 아니다. 넣을 수 있다.)
        // 디스패쳐는 총 4개가 있다.
        // Default : CPU 처리가 중점적일 떄 사용. (ex: 연산, 정렬)
        // IO : 입출력이 중점적일 때 사용. (ex: 통신, 파일 읽기쓰기, DB 등등)
        // Main : UI 업데이트가 중점적일 때 사용.
        // Unconfined : 얘는 특이하다. 뭔가 딱 잡혀있는 것은 없는데 설명만 간략하게 적겠다. 호출한 컨텍스트를 기본으로 사용하는데 중단 후 다시 실행될 때 컨텍스트가 바뀌면 바뀐 컨텍스트를 따라간다.
//        val job = CoroutineScope(Dispatchers.Default).launch {
//            val job1 = launch {
//                repeat(10) {
//                    delay(500)
//                    Log.i("코루틴 job", it.toString())
//                }
//            }
//        }

        // Thread.stop()과 같은 느낌의 메소드다.(물론 지금은 deprecated 되었으니 interrupt가 비슷한 개념일 듯.)
        // cancel을 하게되면 하위 코루틴(job1)도 다 멈춰버리게 된다.
        //job.cancel()
//=================================================================

// 3. 코루틴 join 사용법.
//=================================================================
        // join은 여러 launch가 있는 경우 유용한데 모두 동시 실행되기 때문에 순서를 정할 수 없는 경우 join을 이용하면 순차적으로 실행이 가능해진다.
//        CoroutineScope(Dispatchers.Default).launch {
//            launch {
//                for (i in 0..5) {
//                    delay(500)
//                    Log.i("코루틴", i.toString())
//                }
//            }.join()
//
//            launch {
//                for(i in 6..10) {
//                    delay(500)
//                    Log.i("코루틴", i.toString())
//                }
//            }
//        }

// 4. async await 사용법
//=================================================================
        // 스코프의 종류도 여러가지가 있는데 그냥 바로 사용하는 launch 말고 async라는 것이 있다.
        // async의 최대장점은 await을 이용해서 join처럼 일일이 지정하지 않고 async로 함수를 정의한 후 await으로 언제든지 값을 동기적으로 받게 된다.
        // 가장 인상깊었던 작업이다. 웹도 하는데 async await과 사용방식이 매우 흡사해서 나는 가장 편하게 사용할 수 있을 것 같다.

//        GlobalScope.launch(Dispatchers.Main) {
//            val job1 = async(Dispatchers.IO) {
//                var total = 0
//
//                repeat(10) {
//                    total += it
//                    delay(100)
//                }
//                Log.i("job1", "job1")
//                total
//            }
//
//            val job2 = async(Dispatchers.Main) {
//                var total = 0
//
//                repeat(10) {
//                    delay(100)
//                    total += it
//                }
//                Log.i("job1", "job1")
//                total
//            }
//
//            val result1 = job1.await()
//            val result2 = job2.await()
//
//            Log.i("async await 결과값", (result1 + result2).toString())
//        }
//=================================================================

// 5. withContext 사용법
//=================================================================
        // async await을 사용하는 방법도 있지만 굳이 따로 나눠서 쓰지 않고 한꺼번에 적용하고 싶으면 이렇게 withContext를 사용하면 된다.
        // 물론 웹도 익숙한 개발자라면 async await도 하나의 좋은 수단이 되겠지만 이 방법을 더 많이 사용할 듯 싶다.
//        GlobalScope.launch {
//            val v = withContext(Dispatchers.Main) {
//                var total = 0
//
//                repeat(10) {
//                    delay(100)
//                    total += it
//                }
//
//                total
//            }
//
//            Log.i("withContext", "total 값 : " + v)
//        }
//=================================================================

// 6. 채널
//=================================================================
        // 코루틴을 사용하면서 연산된 값을 다른 코루틴으로 보내고 싶을 때 사용하는 것이 바로 채널이다.
        // 채널을 무분별하게 사용하다가는 큰일나겠다...
//        val channel = Channel<Int>()
//        CoroutineScope(Dispatchers.Main).launch {
//            repeat((1..6).count()) {
//                channel.send(it)
//            }
//
//            repeat(6) {
//                Log.i("channel", "받은 값 : " + channel.receive())
//            }
//        }

//=================================================================
    }
//=================================================================

// 코루틴 내부에서 실행되는 함수는 앞에 suspend를 붙여줘야된다.
//=================================================================
//    private suspend fun suspendFunction(): Int {
//        val value: Int = GlobalScope.async(Dispatchers.IO) {
//            var total = 0
//
//            repeat(10) {
//                total += it
//            }
//
//            Log.i("suspendFunction", "total 값 : " + total)
//            total
//        }.await()
//
//        return value
//    }
}