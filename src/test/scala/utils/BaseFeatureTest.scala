package utils

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

trait BaseFeatureTest extends AnyFeatureSpec with GivenWhenThen with Matchers
