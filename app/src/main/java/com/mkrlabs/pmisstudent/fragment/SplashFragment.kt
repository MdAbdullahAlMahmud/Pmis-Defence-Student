package com.mkrlabs.pmisstudent.fragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mkrlabs.pmisstudent.R
import com.mkrlabs.pmisstudent.util.SharedPref
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    @Inject lateinit var  mAuth :FirebaseAuth
    lateinit var  sharedPref: SharedPref
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref  = SharedPref(view.context)
        lifecycleScope.launchWhenCreated {
            gotToHomeFragment()
        }
    }

    private  suspend  fun  gotToHomeFragment(){
        mAuth.currentUser?.let {
            sharedPref.setStudenUSER_UID(it.uid)
        }
        delay(3000)

        if(mAuth.currentUser!=null){
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        }else{
            findNavController().navigate(R.id.action_splashFragment_to_signUpFragment)
        }
    }

}