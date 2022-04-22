package com.example.vimechecker.view.recyclerview

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vimechecker.R
import com.example.vimechecker.databinding.AchievementItemBinding
import com.example.vimechecker.model.achievement.Achievement
import com.example.vimechecker.model.achievement.player.PAchievement
import com.example.vimechecker.retrofit.Transcriptions

class AchievementsAdapter: RecyclerView.Adapter<AchievementsAdapter.AchievementsViewHolder>() {
    private var achievementsList = emptyList<Achievement>()
    private var IDlist = emptyList<PAchievement>()

    class AchievementsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = AchievementItemBinding.bind(view)
        fun bind(achievement: Achievement) {
            //Log.d("Test", achievement.title)
            /*Каждый bind проверяем на chapter-разделение, но не меняя achievementChapterName*/
            var chapter = ""
            binding.achievementChapterName.visibility = View.GONE

            if(achievement.title.contains("|")) {
                Log.d("Title1", achievement.title)
                chapter = achievement.title.split("|")[1]

                binding.achievementChapterName.text = Transcriptions.localeNames[chapter]
                binding.achievementChapterName.visibility = View.VISIBLE
            }
            if(!achievement.title.contains("+ ")) {
                binding.achievementName.setTextColor(Color.parseColor("#9E9C9C"))
                binding.achievementDescTextView.setTextColor(Color.parseColor("#9E9C9C"))
                binding.achievementRewardTextView.setTextColor(Color.parseColor("#9E9C9C"))
            }
            else {
                binding.achievementName.setTextColor(Color.parseColor("#FFFFFF"))
                binding.achievementDescTextView.setTextColor(Color.parseColor("#FFFFFF"))
                binding.achievementRewardTextView.setTextColor(Color.parseColor("#FFFFFF"))
            }

            val rewardText = "Награда: ${achievement.reward} коинов"
            //Log.d("Test", "${achievement.title.removeSuffix("|$chapter")} ${achievement.title}")
            binding.achievementName.text = achievement.title.removeSuffix("|$chapter")
            binding.achievementDescTextView.text = achievement.description[0]
            binding.achievementRewardTextView.text = rewardText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.achievement_item, parent, false)
        return AchievementsViewHolder(view)
    }

    override fun onBindViewHolder(holder: AchievementsViewHolder, position: Int) {
        holder.bind(achievementsList[position])
    }

    override fun getItemCount(): Int {
        return achievementsList.size
    }

    fun loadAchievements(listOfServerAchievements: List<Achievement>) {
        achievementsList = listOfServerAchievements
    }
}