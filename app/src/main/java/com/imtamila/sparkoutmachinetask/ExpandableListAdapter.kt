package com.imtamila.sparkoutmachinetask

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.imtamila.sparkoutmachinetask.databinding.LayoutChildListItemBinding
import com.imtamila.sparkoutmachinetask.databinding.LayoutEquipmentListItemBinding


class ExpandableListAdapter(val context: Context) :
    RecyclerView.Adapter<ExpandableListAdapter.ExpandableViewHolder>() {
    private var equipmentDataList: ArrayList<EquipmentData>? = null

    private var currentPosition = 0

    internal fun submitList(equipmentDataList: ArrayList<EquipmentData>) {
        this.equipmentDataList = equipmentDataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpandableViewHolder {
        return ExpandableViewHolder(
            LayoutEquipmentListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int = equipmentDataList?.size ?: 0

    override fun onBindViewHolder(holder: ExpandableViewHolder, position: Int) {
        val equipmentData = equipmentDataList?.get(position)
        equipmentData?.let {
            holder.bind(it, position)
        }
    }

    inner class ExpandableViewHolder(private val layoutEquipmentListItemBinding: LayoutEquipmentListItemBinding) :
        RecyclerView.ViewHolder(layoutEquipmentListItemBinding.root) {
        fun bind(equipmentData: EquipmentData, position: Int) {
            with(layoutEquipmentListItemBinding) {
                this.equipmentData = equipmentData
                executePendingBindings()

                with(layoutChild) {
                    removeAllViews()
                    equipmentData.category.forEachIndexed { _, categoryData ->
                        val layoutChildListItemBinding: LayoutChildListItemBinding =
                            DataBindingUtil.inflate(
                                (context as AppCompatActivity).layoutInflater,
                                R.layout.layout_child_list_item,
                                this,
                                false
                            )
                        layoutChildListItemBinding.apply {
                            this.categoryData = categoryData
                            executePendingBindings()
                        }
                        addView(layoutChildListItemBinding.root)
                    }
                }

                //if the position is equals to the item position which is to be expanded

                if (equipmentData.isExpanded) {
                    //creating an animation
                    val slideDown: Animation =
                        AnimationUtils.loadAnimation(context, R.anim.slide_down)

                    //toggling visibility
                    layoutChild.visibility = View.VISIBLE

                    //adding sliding effect
//                    layoutChild.startAnimation(slideDown)
                } else layoutChild.visibility = View.GONE

                ivDropArrow.setOnClickListener { //getting the position of the item to expand it
                    equipmentDataList?.set(
                        position,
                        equipmentData.apply { isExpanded = !isExpanded })
                    //reloading the list
                    notifyDataSetChanged()
                }
            }
        }
    }
}