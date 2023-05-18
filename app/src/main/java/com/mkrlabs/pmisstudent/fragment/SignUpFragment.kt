package com.mkrlabs.pmisstudent.fragment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mkrlabs.pmisstudent.R
import com.mkrlabs.pmisstudent.databinding.FragmentSignUpBinding
import com.mkrlabs.pmisstudent.model.Student
import com.mkrlabs.pmisstudent.model.Teacher
import com.mkrlabs.pmisstudent.model.UserType
import com.mkrlabs.pmisstudent.util.CommonFunction
import com.mkrlabs.pmisstudent.util.Resource
import com.mkrlabs.pmisstudent.util.SharedPref
import com.mkrlabs.pmisstudent.view_model.AuthenticationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {


    lateinit var binding: FragmentSignUpBinding
    lateinit var authViewModel : AuthenticationViewModel


    lateinit var student:Student

     val TAG :String = "Auth"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]


        binding.alreadyhaveAnAccountButton.setOnClickListener {

            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)

        }


        binding.signUpSignUpButton.setOnClickListener {


            val name = binding.signUpNameEdt.text.toString()
            val email = binding.signUpEmailEdt.text.toString()
            val student_id = binding.signUpIDEdt.text.toString()
            val password = binding.signUpPasswordEdt.text.toString()
            val confirm_password = binding.signUpConfirmPasswordEdt.text.toString()


            if (TextUtils.isEmpty(name)){
                binding.signUpNameEdt.error= "required"
                return@setOnClickListener
            }

             if (TextUtils.isEmpty(email)){
                binding.signUpEmailEdt.error= "required"
                return@setOnClickListener
            }

             if (TextUtils.isEmpty(student_id)){
                binding.signUpIDEdt.error= "required"
                return@setOnClickListener
            }

             if (TextUtils.isEmpty(password)){
                binding.signUpPasswordEdt.error= "required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(confirm_password)){
                binding.signUpConfirmPasswordEdt.error= "required"
                return@setOnClickListener
            }


            if (!password.equals(confirm_password)){
                CommonFunction.successToast(view.context,"Password must be same")
            }

              student = Student(name,email,confirm_password,student_id,"","","",UserType.STUDENT)
            authViewModel.createUserAccount(student)

        }



        authViewModel.authState.observe(viewLifecycleOwner, Observer { response->

            when(response){
                is  Resource.Success->{
                    hideLoading(view.context)
                    response.data?.let { result ->
                        val sharedPref = SharedPref(view.context)
                        sharedPref.setLoggedInUserName(student.name)
                        CommonFunction.successToast(view.context,"Account created successfully")
                    }
                }
                is Resource.Error->{
                    hideLoading(view.context)
                    response.message?.let {message->

                        Log.e(TAG,"An error occured $message")
                    }
                }

                is  Resource.Loading->{
                    showLoading(view.context)
                }

            }
        })


        authViewModel.savedUserData.observe(viewLifecycleOwner, Observer { response->

            when(response){
                is  Resource.Success->{
                    hideLoading(view.context)
                    response.data?.let { result ->

                        findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
                        CommonFunction.successToast(view.context,"Saved user data successfully")
                    }
                }
                is Resource.Error->{
                    hideLoading(view.context)
                    response.message?.let {message->

                        Log.e(TAG,"An error occured $message")
                    }
                }

                is  Resource.Loading->{
                    showLoading(view.context)
                }

            }
        })

    }


    fun hideLoading(context: Context){
        binding.signUpProgressBar.visibility= View.GONE
    }

    fun showLoading(context: Context){
        binding.signUpProgressBar.visibility= View.VISIBLE
    }




}