package com.demo.drawland

import android.view.View
import com.demo.drawland.aop.Permissions
import com.hjq.shape.view.ShapeButton
import com.demo.drawland.aop.SingleClick
import com.demo.drawland.app.AppActivity
import com.hjq.permissions.Permission


class MainActivity : AppActivity() {

    private val bidInsView: ShapeButton? by lazy { findViewById(R.id.btn_bid_ins) }
    private val surveyDamageView: ShapeButton? by lazy { findViewById(R.id.btn_survey_damage) }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        setOnClickListener(bidInsView, surveyDamageView)
    }

    override fun initData() {

    }

    @SingleClick
    override fun onClick(view: View) {
        when(view){
            bidInsView->{
                extracted()
            }
            surveyDamageView->{
                startActivity(SurveyDamageActivity::class.java)
            }
        }
    }

    @Permissions(Permission.ACCESS_FINE_LOCATION,Permission.ACCESS_COARSE_LOCATION,Permission.ACCESS_BACKGROUND_LOCATION)
    private fun extracted() {
        startActivity(DrawLandActivity::class.java)
    }
}