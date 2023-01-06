package ru.kirilin.skillswap.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.isActive
import ru.kirilin.skillswap.R
import ru.kirilin.skillswap.config.launchIO
import ru.kirilin.skillswap.config.withMain
import ru.kirilin.skillswap.data.model.api.FileApi.Companion.createMultipartBodyPart
import ru.kirilin.skillswap.data.model.RetrofitModule
import ru.kirilin.skillswap.data.model.User
import ru.kirilin.skillswap.ui.adapter.RequirementAdapter
import ru.kirilin.skillswap.ui.adapter.SkillAdapter
import ru.kirilin.skillswap.ui.viewmodel.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


const val PICK_IMAGE = 100

class MainFragment(
    val mainViewModel: MainViewModel,
    val accountId: String,
) : BaseFragment() {

    lateinit var username: TextView
    lateinit var login: TextView
    lateinit var age: TextView
    lateinit var requirements: Button
    lateinit var skills: Button
    lateinit var search: Button
    lateinit var addSkillBtn: Button
    lateinit var addReqBtn: Button
    lateinit var avatar: ImageView

    lateinit var skillViewModel: SkillViewModel
    lateinit var skillAdapter: SkillAdapter

    lateinit var requirementVM: RequirementViewModel
    lateinit var requirementAdapter: RequirementAdapter

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Coroutine exception, scope active:${coroutineScope.isActive}", throwable)
        coroutineScope = createCoroutineScope()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.main_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        username = view.findViewById(R.id.username)
        login = view.findViewById(R.id.login)
        age = view.findViewById(R.id.age)
        requirements = view.findViewById(R.id.myRequirements)
        skills = view.findViewById(R.id.mySkills)
        search = view.findViewById(R.id.comradeSearch)
        addSkillBtn = view.findViewById(R.id.addSkillBtn)
        addReqBtn = view.findViewById(R.id.addReqBtn)
        avatar = view.findViewById(R.id.avatar)

        val skillRecyclerView = view.findViewById<RecyclerView>(R.id.rv_skills)
        val requirementRecyclerView = view.findViewById<RecyclerView>(R.id.rv_requirements)

        setSkillAdapter(skillRecyclerView)
        setRequirementAdapter(requirementRecyclerView)

        mainViewModel.registrationState.observe(viewLifecycleOwner) {
            username.text = it.login
            login.text = it.login
            age.text = it.age.toString()
            setLogo(it)
        }

        addSkillBtn.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(
                    R.id.container,
                    SkillEditFragment(),
                    SkillEditFragment::class.java.simpleName
                )
                ?.addToBackStack("")
                ?.commit()
        }

        addReqBtn.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(
                    R.id.container,
                    RequirementEditFragment(),
                    RequirementEditFragment::class.java.simpleName
                )
                ?.addToBackStack("")
                ?.commit()
        }

        avatar.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE)
        }
    }

    private fun setLogo(it: User) {
        if (it.logoId != null) {
            coroutineScope.launchIO(
                action = {
                    val response = RetrofitModule.fileApi.getFileById(it.logoId!!)
                    val imageLogo = BitmapFactory.decodeStream(response.byteStream())
                    withMain {
                        avatar.setImageBitmap(imageLogo)
                    }
                },
                onError = {
                    showToast("failed when trying to load logo!")
                }
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK)
            when (requestCode) {
                PICK_IMAGE -> {
                    try {
                        val inputStream =
                            activity?.contentResolver?.openInputStream(Uri.parse(data?.dataString))
                        val imgFile = copyFile(inputStream!!)
                        val options = BitmapFactory.Options()
                        options.inJustDecodeBounds = true
                        val bitmap = BitmapFactory.decodeFile(imgFile.path, options)
                        if (options.outWidth < 0 || options.outHeight < 0) {
                            showToast("it is doesn't look like image, repeat your attempt, please")
                            return
                        }
                        lifecycleScope.launchIO(
                            action = {
                                val logoId = RetrofitModule.fileApi.uploadNewFile(createMultipartBodyPart(imgFile))
                                mainViewModel.updateUserLogo(logoId)
                            },
                            onError = {
                                showToast("unable to sent file to server!")
                            }
                        )
//                        withMain { avatar.setImageBitmap(bitmap) }

                    } catch (e: IOException) {
                        Log.i("TAG", "Some exception detected: $e")
                    }
                }
            }
    }

    private fun copyFile(input: InputStream): File {
        val tempFile = File.createTempFile("new_one", "temp")
        val buffer = ByteArray(1024)
        var bytesRead: Int
        val outputStream = FileOutputStream(tempFile)
        while (input.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        return tempFile
    }

    override fun onResume() {
        skillViewModel.refresh()
        requirementVM.refresh()
        super.onResume()
    }

    private fun setSkillAdapter(recyclerView: RecyclerView) {
        skillAdapter = SkillAdapter(activity)
        skillViewModel = ViewModelProvider(this, SkillViewModelFactory(accountId))
            .get(SkillViewModel::class.java)
        skillViewModel.skillData.observe(this.viewLifecycleOwner) {
            skillAdapter.submitList(skillViewModel.skillData.value!!)
        }

        recyclerView.layoutManager = LinearLayoutManager(
            this.context,
            RecyclerView.VERTICAL, false
        )
        recyclerView.addItemDecoration(
            DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        )

        object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                Toast.makeText(activity, "on Move", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                Toast.makeText(activity, "Skill removed!", Toast.LENGTH_SHORT).show()
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.adapterPosition
                skillViewModel.removeSkill(position)
            }
        }.also {
            val itemTouchHelper = ItemTouchHelper(it)
            itemTouchHelper.attachToRecyclerView(recyclerView)
        }
        recyclerView.adapter = skillAdapter
    }

    private fun setRequirementAdapter(recyclerView: RecyclerView) {
        requirementAdapter = RequirementAdapter()
        requirementVM = ViewModelProvider(this, RequirementViewModelFactory(accountId))
            .get(RequirementViewModel::class.java)
        requirementVM.requirementData.observe(this.viewLifecycleOwner) {
            requirementAdapter.submitList(requirementVM.requirementData.value!!)
        }

        recyclerView.layoutManager = LinearLayoutManager(
            this.context,
            RecyclerView.VERTICAL, false
        )
        recyclerView.addItemDecoration(
            DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        )
        object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                Toast.makeText(activity, "on Move", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                Toast.makeText(activity, "Requirement removed", Toast.LENGTH_SHORT).show()
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.adapterPosition
                requirementVM.removeRequirement(position)
            }
        }.also {
            val itemTouchHelper = ItemTouchHelper(it)
            itemTouchHelper.attachToRecyclerView(recyclerView)
        }
        recyclerView.adapter = requirementAdapter
    }

    companion object {
        private val TAG = MainFragment::class.java.simpleName
    }
}