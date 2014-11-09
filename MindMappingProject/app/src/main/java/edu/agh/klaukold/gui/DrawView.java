package edu.agh.klaukold.gui;

import java.util.ArrayList;
import java.util.List;

import edu.agh.R;
import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Line;
import edu.agh.klaukold.common.Point;
import edu.agh.klaukold.enums.Position;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

import org.xmind.core.INotes;
import org.xmind.core.IPlainNotesContent;
import org.xmind.core.style.IStyle;
import org.xmind.ui.style.Styles;

public class DrawView extends RelativeLayout {
    private Paint paint = new Paint();
    public List<Line> lines;
    public float transx, transy, zoomx = 1, zoomy = 1;
    private int[] position = new int[2];
    private static int activeColor;
    private static int collapsedColor;
    private Canvas canvas;
    public int left = 0;
    public int right = 0;
    public static int LUheight = 0;
    public static int LDHehight = 0;
    public static int RUheight = 0;
    public static int RDHehight = 0;
    public static int count = 0;
    boolean UL = true;
    boolean UR = true;

    // private LinkedList<Style> style = new LinkedList<Style>();

    public DrawView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        getLocationInWindow(position);
        setWillNotDraw(false);
    }
    public Context context;

    private boolean revalidate = true;

    public void revalidate() {
        revalidate = true;

    }

//   //próby wymuszenia odstępów na bloczkach swobodnych
//   private void triggerDetachMove(Box box) {
//	   Core c = MainActivity.core;
//	   if(box.rect.left <= c.rect.left) {
//		   if(!c.left.isEmpty()) {
//			   triggerDetachMoveLeft(box);
//		   }
//	   } else if(box.rect.left >= c.rect.right) {
//		   if(!c.right.isEmpty()) {
//			   triggerDetachMoveRight(box);
//		   }
//	   }
//   }
//
//   private void triggerDetachMoveLeft(Box box) {
//	   Core c = MainActivity.core;
//
//	   Box NW = c.left.get(0);
//	   if(c.left.size() > 1) {
//		   NW = c.left.get(1);
//	   }
//
//	   int diff = (int) (box.maxY - NW.maxY);
//	   if(diff <= 0) {
//		   detMoveLeftUp.add(box);
//	   }
//
//	   NW = c.left.get(c.left.size()-1);
//
//	   diff = (int) (box.maxY - NW.maxY);
//	   if(diff > 0) {
//		   detMoveLeftDown.add(box);
//	   }
//   }

//   private void triggerDetachMoveRight(Box box) {
//	   Core c = MainActivity.core;
//
//	   Box NW = c.right.get(0);
//	   if(c.right.size() > 1) {
//		   NW = c.right.get(1);
//	   }
//
//	   int diff = (int) (box.maxY - NW.maxY);
//	   if(diff <= 0) {
//		   detMoveRightUp.add(box);
//	   }
//
//	   NW = c.right.get(c.right.size()-1);
//
//	   diff = (int) (box.maxY - NW.maxY);
//	   if(diff > 0) {
//		   detMoveRightDown.add(box);
//	   }
//   }

//   public void triggerLastDetachMove() {
//	   List<Box> det = MainActivity.core.detached;
//
//	   if(det.isEmpty()) {
//		   return;
//	   }
//
//	   Box b = det.get(det.size()-1);
//	   b.update();
//	   b.updateMaxY();
//	   b.updateMinY();
//	   triggerDetachMove(b);
//   }

    @Override
    public void onDraw(Canvas canvas) {
        this.canvas = canvas;
        //przesunięcie mapy i wyskalowanie
        canvas.translate(transx, transy);
        canvas.scale(zoomx, zoomy);

        Box root = MainActivity.root;
        if (root == null) {
            return;
        }
        count = root.getChildren().size();
        if (count%2 == 0) {
            left = count/2;
            right = count/2;
        } else {
            left = count/2;
            right = count/2 + 1;
        }


        drawBox(root, canvas);
        if (LUheight == 0 && LDHehight == 0 && RUheight == 0 &&  RDHehight == 0) {
            LUheight = root.getDrawableShape().getBounds().centerY();
            LDHehight = root.getDrawableShape().getBounds().centerY();
            RUheight = root.getDrawableShape().getBounds().centerY();
            RDHehight = root.getDrawableShape().getBounds().centerY();
        }
        for (Box box : root.getChildren()) {
            fireDrawChildren(box, canvas);
        }

//       if(root.isExpanded()) {
//    	   showLines(root);
//       } else {
//    	   //hideLines(root);
//       }
//
//       //jak jest włączony tryb przesuwania, to nic się nie zmienia, nie trzeba liczyć na nowo
        if (MainActivity.mActionMode != null && MainActivity.mActionMode.getTitle().toString().equalsIgnoreCase("move")) {
            //  paint.setStrokeWidth(0);
//
            // drawBox(root, canvas);
//
            for (Box box : root.getChildren()) {
                drawBox(box, canvas);
            }


//            for (Box box : root.getLeftChildren()) {
//                fireDrawChildren(box, canvas);
//            }
//
//            for (Box box : root.getRightChildren()) {
//                fireDrawChildren(box, canvas);
//            }
//
//            for (Box box : root.getDetached()) {
//                drawBox(box, canvas);
//            }
//
//            for (Box box : root.getDetached()) {
//                fireDrawChildren(box, canvas);
//            }

            return;
        }

//        if (!root.getChildren().isEmpty() && updateLeft) {
//            for (Box box : root.getDetached()) {
//                // triggerDetachMove(box);
//            }
//            updateLeft = false;
//        }
//
//       if(!root.right.isEmpty() && updateRight) {
//    	   for(Box box: root.detached) {
//    		 //  triggerDetachMove(box);
//    	   }
//    	   updateRight = false;
//       }
//
//       for(Box box: root.left) {
//    	   if(box.isVisible()) {
//    		   //updateBoxWithText(box);
//    		   box.updateMaxY();
//    		   box.updateMinY();
//
//    		   if(box.isExpanded()) {
//    			 //  showLines(box);
//    		   } else {
//    			   //hideLines(box);
//    		   }
//    	   } else {
//    		  // hideLines(box);
//    	   }
//       }
//
//       for(Box box: root.right) {
//    	   if(box.isVisible()) {
//    		  // updateBoxWithText(box);
//    		   box.updateMaxY();
//    		   box.updateMinY();
//
//    		   if(box.isExpanded()) {
//    			  // showLines(box);
//    		   } else {
//    			   //hideLines(box);
//    		   }
//    	   } else {
//    		  // hideLines(box);
//    	   }
//       }
//
//       for(Box box: root.right) {
//    	  // firePrepareChildren(box);
//       }
//
//       for(Box box: root.left) {
//    	 //  firePrepareChildren(box);
//       }
//
//       for(Box box: root.detached) {
//		  // updateBoxWithText(box);
//		   box.updateMaxY();
//		   box.updateMinY();
//
//		   if(box.isExpanded()) {
//			//   showLines(box);
//		   } else {
//			 //  hideLines(box);
//		   }
//       }
//
//       for(Box box: root.detached) {
//    	   //firePrepareChildren(box);
//       }
//
//       if(revalidate) {
//    	   revalidate = false;
//    	   if(!root.left.isEmpty()) {
//    		   setOffsets(root.left, Position.LEFT, root);
//    	   }
//
//    	   if(!root.right.isEmpty()) {
//    		   setOffsets(root.right, Position.RIGHT, root);
//    	   }
//
//    	   for(Box box: root.left) {
//    		   setOffsets(box.position, box);
//    	   }
//
//    	   for(Box box: root.right) {
//    		   setOffsets(box.position, box);
//    	   }
//
//    	   for(Box box: root.detached) {
//    		   setOffsets(box.position, box);
//    	   }
//
//    	   for(Box box: root.left) {
//    		   adjustToChildren(box);
//    	   }
//
//    	   for(Box box: root.right) {
//    		   adjustToChildren(box);
//    	   }
//
//    	   for(Box box: root.detached) {
//    		   adjustToChildren(box);
//    	   }
//
//    	   //próba wymuszenia odstępów na bloczkach swobodnych
//
//    	   int maxLeft = 10000, maxRight = 10000;
//    	   List<Box> temp = new ArrayList<Box>();
//    	   temp.addAll(root.left);
//
//    	   while(!temp.isEmpty()) {
//    		   Box box = temp.get(0);
//    		   if(box.getChildren() != null && !box.getChildren().isEmpty()) {
//    			   temp.addAll(box.getChildren());
//    		   } else {
//    			   if(box.rect.left < maxLeft) {
//    				   maxLeft = box.rect.left;
//    				   maxRight = box.rect.right;
//    			   }
//    		   }
//
//    		   temp.remove(0);
//    	   }
//
//    	   if(!root.left.isEmpty()) {
//    		   Box NW = root.left.get(0);
//    		   int maxDiff = -10;
//
//    		   for(Box box: detMoveLeftUp) {
//    			   int diff = (int)(box.minY - NW.maxY);
//    			   if(diff > maxDiff) {
//    				   maxDiff = diff;
//    			   }
//    		   }
//
//    		   for(Box box: detMoveLeftUp) {
//    			   Utils.moveChildY(box, -maxDiff -20);
//    			   if(maxLeft < box.rect.right && box.rect.right < maxRight) {
//    				   Utils.moveChildX(box, (maxLeft - box.rect.right - 20));
//    			   }
//    		   }
//
//    		   NW = root.left.get(root.left.size()-1);
//    		   maxDiff = -10;
//
//    		   for(Box box: detMoveLeftDown) {
//    			   int diff = (int)(NW.minY - box.maxY);
//    			   if(diff > maxDiff) {
//    				   maxDiff = diff;
//    			   }
//    		   }
//
//    		   for(Box box: detMoveLeftDown) {
//    			   Utils.moveChildY(box, maxDiff + 20);
//    			   if(maxLeft < box.rect.right && box.rect.right < maxRight) {
//    				   Utils.moveChildX(box, (maxLeft - box.rect.right - 20));
//    			   }
//    		   }
//    	   }
//
//    	   if(!root.right.isEmpty()) {
//    		   Box NW = root.right.get(0);
//    		   int maxDiff = -10;
//
//    		   for(Box box: detMoveRightUp) {
//    			   int diff = (int)(box.minY - NW.maxY);
//    			   if(diff > maxDiff) {
//    				   maxDiff = diff;
//    			   }
//    		   }
//
//    		   for(Box box: detMoveRightUp) {
//    			   Utils.moveChildY(box, -maxDiff -20);
//    		   }
//
//    		   NW = root.right.get(root.right.size()-1);
//    		   maxDiff = -10;
//
//    		   for(Box box: detMoveRightDown) {
//    			   int diff = (int)(NW.minY - box.maxY);
//    			   if(diff > maxDiff) {
//    				   maxDiff = diff;
//    			   }
//    		   }
//
//    		   for(Box box: detMoveRightDown) {
//    			   Utils.moveChildY(box, maxDiff + 20);
//    		   }
//    	   }
//       }
//
//
//       detMoveLeftUp.clear();
//       detMoveLeftDown.clear();
//       detMoveRightDown.clear();
//       detMoveRightUp.clear();
//
//       paint.setStrokeWidth(5);
//
//       for(Line x: lines) {
//    	   if(x.isVisible()) {
//    		   Log.i("line", x.toString());
//
////    		   if(x.getColor() != 0) {
////    			   paint.setColor(x.getColor());
////    		   } else {
////    			   paint.setColor(Color.BLACK);
////    		   }
//
//    		   //canvas.drawLine(x.getStartX(), x.getStartY(), x.getEndX(), x.getEndY(), paint);
//    	   }
//       }
//
//       paint.setStrokeWidth(0);
//
        //drawBox(root, canvas);
//
//       for(Box box: root.left) {
//    	   drawBox(box, canvas);
//       }
//
//       for(Box box: root.right) {
//    	   drawBox(box, canvas);
//       }
//
//       for(Box box: root.left) {
//    	   //fireDrawChildren(box, canvas);
//       }
//
//       for(Box box: root.right) {
//    	  // fireDrawChildren(box, canvas);
//       }
//
//       for(Box box: root.detached) {
//    	   drawBox(box, canvas);
//       }
//
//       for(Box box: root.detached) {
//    	  // fireDrawChildren(box, canvas);
//       }

    }

    private float getLongest(String[] array) {
        float i = 0;

        for (String k : array) {
            if (paint.measureText(k) > i) {
                i = paint.measureText(k);
            }
        }
        return i;
    }

    private void fireDrawChildren(Box box, Canvas canvas) {
        drawBox(box, canvas);
        Log.w("box", String.valueOf(box.topic.isFolded()));
        for (Box child : box.getChildren()) {
            drawBox(child, canvas);
            fireDrawChildren(child, canvas);
        }
    }

//    private void firePrepareChildren(Box box) {
//    	for(Box child: box.getChildren()) {
//    		if(child.isVisible()) {
//    			//updateBoxWithText(child);
//
//    			if(box.isExpanded()) {
//    				//showLines(box);
//    			} else {
//    				//hideLines(box);
//    			}
//    		} else {
//    			//hideLines(box);
//    		}
//    		firePrepareChildren(child);
//    	}
//    }

    private void setOffsets(Box parent) {
        if (!parent.getChildren().isEmpty()) {
            setOffsets(parent.getChildren(), parent);
            for (Box box : parent.getChildren()) {
                //setOffsets(position, box);
            }
        }
    }

    //wyjustowanie względem rodzica
    private void setOffsets(List<Box> list,  Box parent) {
        Box first = list.get(0);
        int diff = first.getDrawableShape().getBounds().top - parent.getDrawableShape().getBounds().top;
        first.getDrawableShape().getBounds().top -= diff;
        first.getDrawableShape().getBounds().bottom -= diff;

        for (int x = 1; x < list.size(); x++) {
            first = list.get(x);
            diff = first.getDrawableShape().getBounds().top - (list.get(x - 1).getDrawableShape().getBounds().bottom + 20);
            first.getDrawableShape().getBounds().top -= diff;
            first.getDrawableShape().getBounds().bottom -= diff;
        }

        int offset = 0;
        int size = list.size();

        first = list.get(size / 2);
        offset = first.getDrawableShape().getBounds().top - (parent.getDrawableShape().getBounds().top + parent.getDrawableShape().getBounds().bottom) / 2;

        for (Box b : list) {
            b.getDrawableShape().getBounds().top -= offset;
            b.getDrawableShape().getBounds().bottom -= offset;
            updateBoxWithText(b);
        }
    }

//    //wyrównanie do dzieci
//    private void adjustToChildren(Box box) {
//    	Box temp = box;
//    	temp.updateMaxY();
//    	temp.updateMinY();
//
//    	//dojście do najwyższego dziecka
//    	while(!temp.getChildren().isEmpty()) {
//    		temp = temp.getChildren().get(0);
//    	}
//
//    	while(temp.parent != null) {
//    		temp.updateMaxY();
//    		temp.updateMinY();
//	    	//sprawdzamy rodzenstwo i odsuwamy je, jeśli jeszcze nie zostało odsunięte
//	    	List<Box> siblings = temp.parent.getChildren();
//	    	if(temp.parent instanceof Core) {
//	    		if(temp.position == Position.LEFT) {
//	    			siblings = ((Core) temp.parent).left;
//	    		} else {
//	    			siblings = ((Core) temp.parent).right;
//	    		}
//	    	}
//	    	//od 1 bo temp to pierwsze dziecko
//
//	    	for(int i = 1; i < siblings.size(); i++) {
//    			siblings.get(i).updateMaxY();
//    			siblings.get(i).updateMinY();
//	    		int sibDiff = (int) (siblings.get(i-1).minY - siblings.get(i).maxY);
//	    		if(sibDiff >= 0) {
//	    			int diff = (int) (siblings.get(i).maxY - siblings.get(i-1).minY);
//	    			Utils.moveChildY(siblings.get(i), -diff + 20);
//	    			siblings.get(i).updateMaxY();
//	    			siblings.get(i).updateMinY();
//	    		}
//	    	}
//	    	temp = temp.parent;
//    	}
//
//    	for(Box child: box.getChildren()) {
//    		adjustToChildren(child);
//    	}
//    }

    private void drawBox(Box box, Canvas canvas) {
        if (box.topic.getParent() == null || !box.topic.getParent().isFolded()) {
            //String s = box.getText().getText();
            //String[] parts = s.split("\n");
            //1float f = getLongest(parts);
            //
            IStyle style = MainActivity.workbook.getStyleSheet().findStyle(box.topic.getStyleId());
            if (style.getProperty(Styles.ShapeClass) == Styles.TOPIC_SHAPE_DIAMOND) {
                ((GradientDrawable) ((RotateDrawable) box.getDrawableShape()).getDrawable()).setStroke(Integer.parseInt(remove2LastChars(style.getProperty(Styles.LineWidth))), Integer.parseInt(style.getProperty(Styles.LineColor)));
            } else if (style.getProperty(Styles.ShapeClass) != Styles.TOPIC_SHAPE_UNDERLINE && style.getProperty(Styles.ShapeClass) != Styles.TOPIC_SHAPE_NO_BORDER) {
                ((GradientDrawable) box.getDrawableShape()).setStroke(Integer.parseInt(remove2LastChars(style.getProperty(Styles.LineWidth))), Integer.parseInt(style.getProperty(Styles.LineColor)));
            }

           if (box.point == null || (box.topic.getParent() !=null && !box.topic.getParent().isRoot()))
            {
                calculatePosition(box);
           }
            box.prepareDrawableShape();
            //   }

            if (box.isSelected) {
                box.setActiveColor();
            }
                box.getDrawableShape().draw(canvas);
                if (style.getProperty(Styles.ShapeClass) != null && style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_UNDERLINE)) {
                    Path path = new Path();
                    path.moveTo(box.getDrawableShape().getBounds().left, box.getDrawableShape().getBounds().bottom);
                    path.lineTo(box.getDrawableShape().getBounds().right, box.getDrawableShape().getBounds().bottom);
                    Paint paint1 = new Paint();
                    paint1.setColor(Integer.parseInt(style.getProperty(Styles.FillColor)));
                    paint1.setStrokeWidth(Integer.parseInt(remove2LastChars(style.getProperty(Styles.LineWidth))));
                    paint1.setStyle(Paint.Style.STROKE);
                    canvas.drawPath(path, paint1);
                }
                drawText(box, canvas);
                // todo sytuacja dla ciemnego tla
                if (box.topic.getNotes().getContent(INotes.PLAIN) != null && !((IPlainNotesContent)box.topic.getNotes().getContent(INotes.PLAIN)).getTextContent().equals("")) {
                    box.newNote = context.getResources().getDrawable(R.drawable.ic_action_view_as_list);
                    box.newNote.setBounds(box.getDrawableShape().getBounds().left, box.getDrawableShape().getBounds().top - 5 - ((BitmapDrawable) box.newNote).getBitmap().getHeight(),
                            box.getDrawableShape().getBounds().left + ((BitmapDrawable) box.newNote).getBitmap().getWidth(), box.getDrawableShape().getBounds().top - 5);
                    box.newNote.draw(canvas);
                } else {
                    box.addNote = context.getResources().getDrawable(R.drawable.ic_action_new_event);
                    box.addNote.setBounds(box.getDrawableShape().getBounds().left, box.getDrawableShape().getBounds().top - 5 - ((BitmapDrawable) box.addNote).getBitmap().getHeight(),
                            box.getDrawableShape().getBounds().left + ((BitmapDrawable) box.addNote).getBitmap().getWidth(), box.getDrawableShape().getBounds().top - 5);
                    box.addNote.draw(canvas);
                }
                box.addBox = context.getResources().getDrawable(R.drawable.ic_action_new);
                box.addBox.setBounds(box.getDrawableShape().getBounds().left + 5 + ((BitmapDrawable) box.addBox).getBitmap().getWidth(), box.getDrawableShape().getBounds().top - 5 - ((BitmapDrawable) box.addBox).getBitmap().getHeight(),
                        box.getDrawableShape().getBounds().left + 5 + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + ((BitmapDrawable) box.addBox).getBitmap().getWidth(), box.getDrawableShape().getBounds().top - 5);
                box.addBox.draw(canvas);
                //todomarkery
//                box.newMarker = context.getResources().getDrawable(R.drawable.ic_action_new_picture);
//                box.newMarker.setBounds(box.getDrawableShape().getBounds().left + 5 + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + ((BitmapDrawable) box.newNote).getBitmap().getWidth() + 5, box.getDrawableShape().getBounds().top - 5 - ((BitmapDrawable) box.newMarker).getBitmap().getHeight(),
//                        box.getDrawableShape().getBounds().left + 5 + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + ((BitmapDrawable) box.newNote).getBitmap().getWidth() + ((BitmapDrawable) box.newMarker).getBitmap().getWidth(), box.getDrawableShape().getBounds().top - 5);
//                box.newMarker.draw(canvas);
                if (!(box.topic.isRoot()) && box.topic.getAllChildren().size() > 0) {
                    if (!box.topic.isFolded()) {
                        box.collapseAction = context.getResources().getDrawable(R.drawable.ic_action_collapse);
                        box.collapseAction.setBounds(box.getDrawableShape().getBounds().left + 5 + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + 5, box.getDrawableShape().getBounds().top - 5 - ((BitmapDrawable) box.collapseAction).getBitmap().getHeight(), box.getDrawableShape().getBounds().left + 5 + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + ((BitmapDrawable) box.collapseAction).getBitmap().getWidth(), box.getDrawableShape().getBounds().top - 5);
                        box.collapseAction.draw(canvas);
                    } else {
                        box.expandAction = context.getResources().getDrawable(R.drawable.ic_action_expand);
                        box.expandAction.setBounds(box.getDrawableShape().getBounds().left + 5 + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + 5, box.getDrawableShape().getBounds().top - 5 - ((BitmapDrawable) box.expandAction).getBitmap().getHeight(), box.getDrawableShape().getBounds().left + 5 + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + ((BitmapDrawable) box.expandAction).getBitmap().getWidth(), box.getDrawableShape().getBounds().top - 5);
                        box.expandAction.draw(canvas);
                    }
                }
//                Rect rect = new Rect();
//                rect.set(box.getDrawableShape().getBounds().left, box.getDrawableShape().getBounds().top - 5 - ((BitmapDrawable) box.newNote).getBitmap().getHeight(),
//                        box.getDrawableShape().getBounds().left + ((BitmapDrawable) box.newNote).getBitmap().getWidth(), box.getDrawableShape().getBounds().top - 5);


//            for (int j = 0; j < parts.length; j++) {
//                paint.setColor(Color.BLACK);
//                String str = parts[j];
//                float start = 0.5f * box.getDrawableShape().getBounds().height() / (parts.length);
//                if (j == 0) {
//                     canvas.drawText(str, (box.getDrawableShape().getBounds().left + (box.getDrawableShape().getBounds().width() - f)/2), box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height())/(parts.length) * (j) + start + paint.getTextSize()/2, paint);
//                } else {
//                    canvas.drawText(str, (box.getDrawableShape().getBounds().left + (box.getDrawableShape().getBounds().width() - f)/2), box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height())/(parts.length) * (j) + start + paint.getTextSize()/2, paint);
//                }
//            }
         //   if (!box.topic.isFolded()) {
                showLines(box);
                if (MainActivity.style.equals("ReadyMap")) {
                    showLines(MainActivity.root);
                }
            if (box.related != null) {
                drawRelationship(box);
            }
         //   }
        }
    }

    // ToDO wyswietlanie tekstu
    private void drawText(Box box, Canvas canvas) {
        IStyle style = MainActivity.workbook.getStyleSheet().findStyle(box.topic.getStyleId());
        paint = new Paint();
        if (box != null) {
            float x = 0;
            float y = 0;
            String s = box.topic.getTitleText();
            String[] parts = s.split("\n");
            float f = getLongest(parts);
            //   if (box.getShape() != BlockShape.NO_BORDER || box.getShape() != BlockShape.UNDERLINE) {
            paint.setColor(Integer.parseInt(style.getProperty(Styles.TextColor)));
            //    }
            paint.setTextSize(Integer.parseInt(remove2LastChars(style.getProperty(Styles.FontSize))));
//            if (box.getText().getAlign() == Align.CENTER) {
//                paint.setTextAlign(Paint.Align.CENTER);
//            } else if (box.getText().getAlign() == Align.RIGHT) {
//                paint.setTextAlign(Paint.Align.RIGHT);
//            } else if (box.getText().getAlign() == Align.LEFT) {
//                paint.setTextAlign(Paint.Align.LEFT);
//            }
////            Log.w(s, rect.toString());
////            Log.w("measute", String.valueOf(paint.measureText(s)));
            Typeface font = Typeface.DEFAULT;
            if (style.getProperty(Styles.FontFamily).equals("Times New Roman")) {
                font = Typeface.SERIF;
            } else if (style.getProperty(Styles.FontFamily).equals("Arial")) {
                font = Typeface.SANS_SERIF;
            } else if (style.getProperty(Styles.FontFamily).equals("Courier New")) {
                font = Typeface.MONOSPACE;
            }

            if (style.getProperty(Styles.FontStyle) == Styles.FONT_STYLE_ITALIC && style.getProperty(Styles.FontStyle) == Styles.FONT_WEIGHT_BOLD) {
                Typeface tf = Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC);
                paint.setTypeface(tf);
                // paint.setFakeBoldText(true);
            } else if (style.getProperty(Styles.FontStyle) == Styles.FONT_STYLE_ITALIC) {
                Typeface tf = Typeface.create(Typeface.SERIF, Typeface.ITALIC);
                paint.setTypeface(tf);
            } else if (style.getProperty(Styles.FontStyle) == Styles.FONT_WEIGHT_BOLD) {
                Typeface tf = Typeface.create(font, Typeface.BOLD);
                paint.setTypeface(tf);

            } else if (style.getProperty(Styles.TextAlign).equals(Styles.ALIGN_LEFT) && style.getProperty(Styles.TextDecoration) == Styles.TEXT_DECORATION_LINE_THROUGH) {
                Typeface tf = Typeface.create(font, Typeface.NORMAL);
                paint.setTypeface(tf);
                paint.setStrokeWidth(2);
                paint.setStrikeThruText(true);
            } else {
                // Typeface tf = Typeface.create(box.getText().typeface);
                paint.setTypeface(font);
            }

            if (style.getProperty(Styles.TextAlign).equals(Styles.ALIGN_CENTER)) {
                paint.setTextAlign(Paint.Align.CENTER);
            } else if (style.getProperty(Styles.TextAlign).equals(Styles.ALIGN_RIGHT)) {
                paint.setTextAlign(Paint.Align.RIGHT);
            } else if (style.getProperty(Styles.TextAlign).equals(Styles.ALIGN_LEFT)) {
                paint.setTextAlign(Paint.Align.LEFT);
            }
            for (int j = 0; j < parts.length; j++) {
                String str = parts[j];
                float start = 0.4f * box.getDrawableShape().getBounds().height() / (parts.length);

                if (parts.length == 1) {
                    Rect rectText = new Rect();
                    paint.getTextBounds(box.topic.getTitleText(), 0, box.topic.getTitleText().length(), rectText);
                    if (style.getProperty(Styles.TextAlign).equals(Styles.ALIGN_CENTER)) {
//                        if (box.getShape() == BlockShape.DIAMOND) {
//                            x = box.getDrawableShape().getBounds().left + box.getWidth() / 2;
//                            y = box.getDrawableShape().getBounds().top + (box.getWidth() / 2);
//                        } else if (box.getShape() == BlockShape.ELLIPSE) {
//                            x = box.getDrawableShape().getBounds().left + box.getWidth() / 2;
//                            y = box.getDrawableShape().getBounds().top + (box.getHeight() / 2);
//                        } else {
                        x = box.getDrawableShape().getBounds().left + rectText.width() / 2 + (box.getDrawableShape().getBounds().width() - rectText.width()) / 2;
                        //  }
                    } else if (style.getProperty(Styles.TextAlign).equals(Styles.ALIGN_RIGHT)) {
//                        if (box.getShape() == BlockShape.DIAMOND) {
//                            x = (box.getDrawableShape().getBounds().right - (box.getWidth() - f) / 2);
//                            y = box.getDrawableShape().getBounds().top + (box.getWidth() / 2);
//                        } else if (box.getShape() == BlockShape.ELLIPSE) {
//                            x = (box.getDrawableShape().getBounds().right - (box.getWidth() - f) / 2);
//                            y = box.getDrawableShape().getBounds().top + (box.getHeight() / parts.length);
//                        } else {
                        x = (box.getDrawableShape().getBounds().right - 10);
//                        }
                    } else if (style.getProperty(Styles.TextAlign).equals(Styles.ALIGN_LEFT)) {
                        x = (box.getDrawableShape().getBounds().left + 10);
                        y = box.getDrawableShape().getBounds().top + rectText.height();
                    }
//                    if (box.getShape() == BlockShape.ELLIPSE || box.getShape() == BlockShape.DIAMOND) {
//                        y = box.getDrawableShape().getBounds().centerY();
//                    }
                    y = box.getDrawableShape().getBounds().centerY();
                    canvas.drawText(str, x, y, paint);

                } else {
                    if (style.getProperty(Styles.TextAlign).equals(Styles.ALIGN_CENTER)) {
                        if (style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_DIAMOND)) {
                            //TODO poprawic
                            x = box.getDrawableShape().getBounds().left + box.getWidth() / 2;
                            y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height()) / (parts.length) * (j) + start + paint.getTextSize() / 2;
                        } else if (style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_ELLIPSE)) {
                            if (j == 0) {
                                y = box.getDrawableShape().getBounds().top + paint.getTextSize() + paint.getTextSize() / 2;
                            } else {
                                y = box.getDrawableShape().getBounds().top + paint.getTextSize() * (j + 1) + paint.getTextSize() / 2;
                                // y = box.getDrawableShape().getBounds().top + (((box.getDrawableShape().getBounds().height()) / ((parts.length) * paint.getTextSize())) * (j + 1)) + 20;
                            }
                            //  start = 0.2f * box.getDrawableShape().getBounds().height() / (parts.length);
                            x = box.getDrawableShape().getBounds().left + box.getWidth() / 2;
                            // y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height()) / (parts.length) * (j) + start + paint.getTextSize();
                        } else {
                            x = box.getDrawableShape().getBounds().left + box.getWidth() / 2;
                            //y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height()) / (parts.length) * (j) * paint.getTextSize();
                            if (j == 0) {
                                y = box.getDrawableShape().getBounds().top + paint.getTextSize();

                            } else {
                                y = box.getDrawableShape().getBounds().top + paint.getTextSize() * (j + 1) + paint.getTextSize() / 2;
                                // y = box.getDrawableShape().getBounds().top + (((box.getDrawableShape().getBounds().height()) / ((parts.length) * paint.getTextSize())) * (j + 1)) + 20;
                            }
                        }
                    } else if (style.getProperty(Styles.TextAlign).equals(Styles.ALIGN_RIGHT)) {
                        if (style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_DIAMOND)) {
                            //TODO poprawic
                            x = (box.getDrawableShape().getBounds().right - (box.getWidth() - f) / 2);
                            y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height()) / (parts.length) * (j) + start + paint.getTextSize() / 2;
                        } else if (style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_ELLIPSE)) {
                            start = 0.2f * box.getDrawableShape().getBounds().height() / (parts.length);
                            x = (box.getDrawableShape().getBounds().right - (box.getWidth() - f) / 2);
                            y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height()) / (parts.length) * (j) + start + paint.getTextSize();
                        } else {
                            x = (box.getDrawableShape().getBounds().right - (box.getWidth() - f) / 2);
                            y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height()) / (parts.length) * (j) + start + paint.getTextSize() / 2;
                        }

                    } else if (style.getProperty(Styles.TextAlign).equals(Styles.ALIGN_LEFT)) {
                        if (style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_DIAMOND)) {
                            //TODO poprawic
                            x = (box.getDrawableShape().getBounds().left + (box.getWidth() - f) / 2);
                            y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height()) / (parts.length) * (j) + start + paint.getTextSize() / 2;
                        } else if (style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_ELLIPSE)) {
                            start = 0.2f * box.getDrawableShape().getBounds().height() / (parts.length);
                            x = (box.getDrawableShape().getBounds().left + (box.getWidth() - f) / 2);
                            y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height()) / (parts.length) * (j) + start + paint.getTextSize();
                        } else {
                            x = (box.getDrawableShape().getBounds().left + (box.getWidth() - f) / 2);
                            y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height()) / (parts.length) * (j) + start + paint.getTextSize() / 2;
                        }
                        //x = (box.getDrawableShape().getBounds().left + (box.getWidth()- f)/2);
                        //y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height())/(parts.length) * (j) + start + paint.getTextSize()/2;
                    }
                    canvas.drawText(str, x, y, paint);
                    //canvas.drawText(str, x, y * (j) + start + paint.getTextSize()/2, paint);
                    //canvas.drawText(str, (box.getDrawableShape().getBounds().left + (box.getDrawableShape().getBounds().width() - f)/2), box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height())/(parts.length) * (j) + start + paint.getTextSize()/2, paint);
                }
            }
        }
    }

    public void updateBox(Box box) {
        if (canvas != null) {
            drawBox(box, canvas);
        }
       // drawText(box, canvas);
    }

    public void updateBoxWithText(Box box) {
        String s = box.topic.getTitleText();
        String[] parts = s.split("\n");

        float f = getLongest(parts);
        drawText(box, canvas);
//        paint = new Paint();
//        if (box != null) {
//            float x = 0;
//            float y = 0;
//            if (box.getShape() != BlockShape.NO_BORDER || box.getShape() != BlockShape.UNDERLINE) {
//                paint.setColor(box.getText().getColor().getColor());
//            }
//            paint.setTextSize(box.getText().getSize());
//            if (box.getText().getAlign() == Align.CENTER) {
//                paint.setTextAlign(Paint.Align.CENTER);
//            } else if (box.getText().getAlign() == Align.RIGHT) {
//                paint.setTextAlign(Paint.Align.RIGHT);
//            } else if (box.getText().getAlign() == Align.LEFT) {
//                paint.setTextAlign(Paint.Align.LEFT);
//            }
//            Log.w(s, rect.toString());
//            Log.w("measute", String.valueOf(paint.measureText(s)));
//            if (box.getText().isItalic() && box.getText().isBold()) {
//                Typeface tf = Typeface.create(box.getText().typeface, Typeface.BOLD_ITALIC);
//                paint.setTypeface(tf);
//                // paint.setFakeBoldText(true);
//            } else if (box.getText().isItalic()) {
//                Typeface tf = Typeface.create(box.getText().typeface, Typeface.ITALIC);
//                paint.setTypeface(tf);
//            } else if (box.getText().isBold()) {
//                Typeface tf = Typeface.create(box.getText().typeface, Typeface.BOLD);
//                paint.setTypeface(tf);
//            } else if (box.getText().isStrikeOut()) {
//                paint.setStrokeWidth(2);
//                paint.setStrikeThruText(true);
//            } else {
//                // Typeface tf = Typeface.create(box.getText().typeface);
//                paint.setTypeface(box.getText().typeface);
//            }
//        box.getDrawableShape().setBounds(box.getDrawableShape().getBounds().left, box.getDrawableShape().getBounds().top,
//             box.getDrawableShape().getBounds().right, box.getDrawableShape().getBounds().top + parts.length * box.getText().getSize() + 10);
//        box.getDrawableShape().getBounds().bottom = box.getDrawableShape().getBounds().top + parts.length * 30 + 10;
//        if (box instanceof Root) {
//              box.setWidth((int) f + 30);
//              box.getDrawableShape().setBounds(box.getDrawableShape().getBounds().left, box.getDrawableShape().getBounds().top,
//                     box.getDrawableShape().getBounds().right + (int) f + 50, box.getDrawableShape().getBounds().bottom);
//              box.getDrawableShape().getBounds().right = box.getDrawableShape().getBounds().left + (int) f + 50;
//       }
//    	else if(box.xmlPosition != null) {
//    		box.rect.right = box.rect.left + (int) f + 50;
//    		box.xmlPosition.x = box.rect.left;
//    		box.xmlPosition.y = box.rect.top;
//    	}
        //else if(box.position == Position.RIGHT) {
//        else {
//            //TODO sprawdzic i poprawic
//            box.getPoint().x = box.getParent().getDrawableShape().getBounds().right + 20;
//            box.setWidth((int) f + 50);
//        }
//    	} else {
//    		box.rect.right = box.parent.rect.left - 20;
//        	box.rect.left = box.rect.right - ((int) f + 50);
//    	}
        Rect rect = new Rect();
        paint.setTextSize((float) Integer.parseInt(remove2LastChars(MainActivity.workbook.getStyleSheet().findStyle(box.topic.getStyleId()).getProperty(Styles.FontSize))));
        paint.getTextBounds(s, 0, s.length(), rect);
        if (parts.length == 1) {
            int w = (rect.right - rect.left) + (rect.right - rect.left) / 2;
            if (w > 120) {
                box.setWidth(w);
            }
            //  box.setWidth(((int) f) * (box.getText().getSize()/3));
            //  box.getDrawableShape().setBounds(box.getDrawableShape().getBounds().left, box.getDrawableShape().getBounds().top,
            //         box.getDrawableShape().getBounds().left + (-rect.right - rect.left), box.getDrawableShape().getBounds().bottom);
            // box.getDrawableShape().getBounds().right = box.getDrawableShape().getBounds().left + (int) f + 50;
        } else {
            int width = 0;
            for (String sPart : parts) {
                paint.getTextBounds(sPart, 0, sPart.length(), rect);
                if (Math.abs((rect.right - rect.left)) > width) {
                    width = Math.abs(rect.right - rect.left);
                }
            }
            if (width > box.getWidth()) {
                box.setWidth(width + Integer.parseInt(remove2LastChars(MainActivity.workbook.getStyleSheet().findStyle(box.topic.getStyleId()).getProperty(Styles.FontSize))));
            }
            box.setHeight((rect.bottom - rect.top) * parts.length + (Integer.parseInt(remove2LastChars(MainActivity.workbook.getStyleSheet().findStyle(box.topic.getStyleId()).getProperty(Styles.FontSize))) / 2 * parts.length));
            if (MainActivity.workbook.getStyleSheet().findStyle(box.topic.getId()) != null && MainActivity.workbook.getStyleSheet().findStyle(box.topic.getId()).equals(Styles.TOPIC_SHAPE_ELLIPSE)) {
                if (parts.length > 1) {
                    box.setHeight((int) (box.getHeight() * Math.sqrt(2)));
                }
                //  box.setWidth(box.getWidth() + box.getWidth() / 2);
                //  box.setHeight(box.getHeight() + box.getHeight() / 2);
            }
//            if (box.getShape() == BlockShape.ELLIPSE) {
//                box.setWidth((int) (f * 1.5));
//                box.setHeight(parts.length * (box.getText().getSize() + 40));
//            } else {
//                box.setWidth((int) f + 30);
//                box.getDrawableShape().setBounds(box.getDrawableShape().getBounds().left, box.getDrawableShape().getBounds().top,
//                        box.getDrawableShape().getBounds().right + (int) f + 50, box.getDrawableShape().getBounds().bottom);
//                box.getDrawableShape().getBounds().right = box.getDrawableShape().getBounds().left + (int) f + 50;
//            }
        }
        for (Box b : box.getLines().keySet()) {
            if (b.position == Position.LFET) {
                box.getLines().get(b).setStart(new Point(box.getDrawableShape().getBounds().left, box.getDrawableShape().getBounds().centerY()));
            } else {
                box.getLines().get(b).setStart(new Point(box.getDrawableShape().getBounds().right, box.getDrawableShape().getBounds().centerY()));
            }
        }
        drawText(box, canvas);
//        if (box.isExpanded()) {
//              showLines(box);
//        }
        //  box.getDrawableShape().draw(canvas);
    }
//    //dodanie linii
//    public void addLine(Box b1, Box b2) {
//    	//Line newLine = new Line(b1, b2);
//
//    	//if(!lines.contains(newLine)) {
//    		//newLine.setColor(b2.color);
//    		//lines.add(newLine);
//    	}
//    }
//
//    //usunięcie linii
//    public void deleteLine(Box b1, Box b2) {
//    	Line newLine = new Line(b1, b2);
//    	if(lines.contains(newLine)) {
//    		lines.remove(newLine);
//    	} else {
//    		newLine = new Line(b2, b1);
//    		if(lines.contains(newLine)) {
//        		lines.remove(newLine);
//        	}
//    	}
//    }

    //aktualizacja wyświetlania linii po przepięciu bloczka i propagacja koloru
    public void updateLineForChildren(Box parent) {
        for (int i = 0; i < lines.size(); i++) {
            Line x = lines.get(i);

            if (!x.isVisible()) {
                continue;
            }
            //todo te linie
//    		if(x.getEnd1().equals(parent)) {
//    			x.setColor(parent.color);
//    			for(Box child: parent.getChildren()) {
//    				child.color = parent.color;
//    				updateLineForChildren(child);
//    			}
//    		}
        }
    }

//    public void updateCore(Root core) {
//        String s = core.topic.getTitleText();
//        String[] parts = s.split("\n");
//
//        float f = getLongest(parts);
//
//        core.getDrawableShape().getBounds().bottom = core.getDrawableShape().getBounds().top + parts.length * 30 + 10;
//        core.getDrawableShape().getBounds().right = core.getDrawableShape().getBounds().left + (int) f + 50;
//    }

//    //usuwanie linii wychodzących z bloczka lub wchodzących do niego i usunięcie bloczka z listy dzieci rodzica
//    private void clear(Box b) {
//    	for(int i = 0; i < lines.size(); i++) {
//    		Line x = lines.get(i);
//    		if(x.getEnd1().equals(b) || x.getEnd2().equals(b)) {
//    			lines.remove(x);
//    			i--;
//    		}
//    	}
//
//    	b.parent.removeChild(b);
//    }
//
//    public void eraseHimAndChildrenFromView(Box b) {
//    	List<Box> temp = b.getChildren();
//
//    	while(!temp.isEmpty()) {
//    		eraseHimAndChildrenFromView(temp.get(0));
//    	}
//
//    	clear(b);
//    }

//    public void deleteHimAndChildren(Box b) {
//    	eraseHimAndChildrenFromView(b);
//    	b.deleteBoxAndChildren();
//    }

    //    private void hideLines(Box b) {
//    	for(int i = 0; i < lines.size(); i++) {
//    		Line x = lines.get(i);
//
//    		if(!x.isVisible()) {
//    			continue;
//    		} else if(x.getEnd1().equals(b)) {
//    			x.setVisible(false);
//    		}
//    	}
//    }
//
    private void showLines(Box box) {
        for (Box b1 : box.getLines().keySet()) {
            Line x = box.getLines().get(b1);
            if (x == null || !x.isVisible()) {
                continue;
            } else {
                Paint paint = new Paint();
                paint.setColor(x.getColor().getColor());
                paint.setStrokeWidth(x.getThickness() + 1);
                paint.setStyle(Paint.Style.STROKE);
                if ((x.shape == Styles.BRANCH_CONN_ELBOW || x.shape == Styles.BRANCH_CONN_ROUNDEDELBOW)) {
                    x.box = box;
                }
                x.preparePath();
                canvas.drawPath(x.getPath(), paint);
//                    if (x.getShape() == LineStyle.ARROWED_CURVE) {
//                        Path path = new Path();
////                        path.moveTo(0, -10);
////                        path.lineTo(5, 0);
////                        path.lineTo(-5, 0);
//                        path.moveTo(x.getEnd().x, x.getEnd().y);
//                        path.lineTo(x.getEnd().x + 10, x.getEnd().y - 20);
//                        path.lineTo(x.getEnd().x - 10, x.getEnd().y - 20);
//                        path.close();
//                     //   path.offset(10, 40);
//                        canvas.drawPath(path, paint);
//                     //   path.offset(50, 100);
//                        path.offset(-10, -20);
//                        canvas.drawPath(path, paint);
//// offset is cumlative
//// next draw displaces 50,100 from previous
//                     //   path.offset(50, 100);
//                        canvas.drawPath(path, paint);
//                    }
                if (MainActivity.EDIT_CONN) {
                    RectF rf = new RectF();
                    x.getPath().computeBounds(rf, true);
                    x.deleteLine = context.getResources().getDrawable(R.drawable.ic_action_cancel);
                    x.deleteLine.setBounds((int) rf.centerX(), (int) rf.centerY(), (int) rf.centerX() + 40, (int) rf.centerY() + 40);
                    x.deleteLine.draw(canvas);
                }
            }
        }

//    	for(int i = 0; i < lines.size(); i++) {
//    		Line x = lines.get(i);
//
//    		if(x.isVisible()) {
//    			continue;
//    		} else if(x.getEnd1().equals(b)) {
//    			x.setVisible(true);
//    		}
//    	}
    }

    private class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 5;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {
        }

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                // I load the next page of gigs using a background task,
                // but you can call any function here.
                //new LoadGigsTask().execute(currentPage + 1);
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

    }

    public void updateText() {
        drawText(MainActivity.boxEdited, canvas);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        // Get the coordinates of the touch event.
//        float eventX = event.getX();
//        float eventY = event.getY();
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                // Set a new starting point
//                //path.moveTo(eventX, eventY);
//                return true;
//            case MotionEvent.ACTION_MOVE:
//                // Connect the points
//                // path.lineTo(eventX, eventY);
//                break;
//            default:
//                return false;
//        }
//
//        // Makes our view repaint and call onDraw
//        invalidate();
//        return true;
//    }


    //    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//
//        Box box = Utils.whichBox(this, event);
//        switch(event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//
//
//                    Log.w("action", "down");
////                for(int i=0; i < rectangles.size(); i++){
////                    View child = rectangles.get(i);
////                    if(x > child.getLeft() && x < child.getLeft()+150 && y > child.getTop() && y < child.getTop()+50){
////                        rectId = child.getId();
////                        rectangles.get(rectId-1).setTop(child.getTop());
////                        rectangles.get(rectId-1).setLeft(child.getLeft());
////                        break;
////                    }
////                }
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.w("action", "move");
////                if(rectId > 0){
////                    rectangles.get(rectId-1).setBackgroundRessource(true);
////                    rectangles.get(rectId-1).setTop(y-25);
////                    rectangles.get(rectId-1).setLeft(x-25);
////                    Log.i("MOVE X", rectId+" "+rectangles.get(rectId-1).getX());
////                    Log.i("MOVE Y", rectId+" "+rectangles.get(rectId-1).getY());
////                }
//
//                break;
//            case MotionEvent.ACTION_UP:
//                Log.w("action", "up");
////                Log.i("rectid Cancel", ""+rectId);
////                if(rectId > 0){
////                    rectangles.get(rectId-1).setBackgroundRessource(true);
////                }
//
//                break;
//        }
//
//        invalidate();
//        return true;
//
//    }
    private String remove2LastChars(String str) {
        return str.substring(0, str.length() - 2);
    }


    private void calculatePosition(Box b) {
        if (b.topic.getParent().isRoot()) {
            int off = 1;
            if (count%8 == 0) {
                off = count/8 + 1;
            }
            if (left < right) {
                    if (!(right % 2 == 0) &&  RDHehight == RUheight ) {
                        b.point = new Point(b.parent.getDrawableShape().getBounds().right + off * 50 , b.parent.getDrawableShape().getBounds().centerY() - b.getHeight()/2);
                        if (b.parent.getLines().get(b) != null) {
                            b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()));
                            b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().right + 50, b.parent.getDrawableShape().getBounds().centerY()));
                        } else {
                            IStyle parentStyle = MainActivity.workbook.getStyleSheet().findStyle(b.parent.topic.getStyleId());
                            Line l = new Line(parentStyle.getProperty(Styles.LineClass), Integer.parseInt(parentStyle.getProperty(Styles.LineWidth).substring(0,parentStyle.getProperty(Styles.LineWidth).length() - 2)), new ColorDrawable(Integer.parseInt(parentStyle.getProperty(Styles.LineColor))),
                                    new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()),
                                    new Point(b.parent.getDrawableShape().getBounds().right + off * 50  + b.getWidth(), b.parent.getDrawableShape().getBounds().centerY()), true);
                            b.parent.getLines().put(b, l);

                        }
                        int h = 0;
                        for (int i=0; i<b.getChildren().size(); i++) {
                            h += b.getChildren().get(i).getHeight() + 35;
                        }
                        RUheight -= (b.getHeight()/2 + h);
                        RDHehight += b.getHeight()/2 + h;
                    } else {
                        if (UR) {
                            b.point = new Point(b.parent.getDrawableShape().getBounds().right + off * 50 , RUheight - 40 - b.getHeight());
                            if (b.parent.getLines().get(b) != null) {
                                b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()));
                                b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().right + 50,RUheight - b.getHeight()/2));
                            } else {
                                IStyle parentStyle = MainActivity.workbook.getStyleSheet().findStyle(b.parent.topic.getStyleId());
                                Line l = new Line(parentStyle.getProperty(Styles.LineClass), Integer.parseInt(parentStyle.getProperty(Styles.LineWidth).substring(0,parentStyle.getProperty(Styles.LineWidth).length() - 2)), new ColorDrawable(Integer.parseInt(parentStyle.getProperty(Styles.LineColor))),
                                        new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()),
                                        new Point(b.parent.getDrawableShape().getBounds().right + off * 50,RUheight - b.getHeight()/2), true);
                                l.off = 10;
                                b.parent.getLines().put(b, l);

                            }
                            int h = 0;
                            for (int i=0; i<b.getChildren().size(); i++) {
                                h += b.getChildren().get(i).getHeight() + 35;
                            }
                            RUheight -= (b.getHeight() + 20 + h);

                          UR = false;
                        } else {
                            b.point = new Point(b.parent.getDrawableShape().getBounds().right + off *  50 , RDHehight + 40);
                            if (b.parent.getLines().get(b) != null) {
                                b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()));
                                b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().right + 50,RDHehight + b.getHeight()));
                            } else {
                                IStyle parentStyle = MainActivity.workbook.getStyleSheet().findStyle(b.parent.topic.getStyleId());
                                Line l = new Line(parentStyle.getProperty(Styles.LineClass), Integer.parseInt(parentStyle.getProperty(Styles.LineWidth).substring(0,parentStyle.getProperty(Styles.LineWidth).length() - 2)), new ColorDrawable(Integer.parseInt(parentStyle.getProperty(Styles.LineColor))),
                                        new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()),
                                        new Point(b.parent.getDrawableShape().getBounds().right + off * 50,RDHehight + b.getHeight()), true);
                                b.parent.getLines().put(b, l);

                            }
                            int h = 0;
                            for (int i=0; i<b.getChildren().size(); i++) {
                                h += b.getChildren().get(i).getHeight() + 35;
                            }
                            RDHehight += b.getHeight() + 20 + h;
                            UR = true;
                        }
                    }
                b.position = Position.RIGHT;
                right--;
            } else {
                if (!(left % 2 == 0) &&  LDHehight == LUheight ) {
                    b.point = new Point(b.parent.getDrawableShape().getBounds().left - off * 50 - b.getWidth(), b.parent.getDrawableShape().getBounds().centerY() - b.getHeight()/2);
                    if (b.parent.getLines().get(b) != null) {
                        b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY()));
                        b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().left - 50 - b.getWidth(), b.parent.getDrawableShape().getBounds().centerY()));
                    } else {
                        IStyle parentStyle = MainActivity.workbook.getStyleSheet().findStyle(b.parent.topic.getStyleId());
                        b.parent.getLines().put(b, new Line(parentStyle.getProperty(Styles.LineClass), Integer.parseInt(parentStyle.getProperty(Styles.LineWidth).substring(0,parentStyle.getProperty(Styles.LineWidth).length() - 2)), new ColorDrawable(Integer.parseInt(parentStyle.getProperty(Styles.LineColor))),
                                new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY()),
                                new Point(b.parent.getDrawableShape().getBounds().left - off *  50, b.parent.getDrawableShape().getBounds().centerY()), true));

                    }
                    int h = 0;
                    for (int i=0; i<b.getChildren().size(); i++) {
                        h += b.getChildren().get(i).getHeight() + 35;
                    }
                    LUheight -= (b.getHeight()/2 + h);
                    LDHehight += b.getHeight()/2 + h;
                } else {
                    if (UL) {
                        b.point = new Point(b.parent.getDrawableShape().getBounds().left - off * 50  - b.getWidth() , LUheight - 40 - b.getHeight());
                        if (b.parent.getLines().get(b) != null) {
                            b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY()));
                            b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().left - 50 - b.getHeight(),LUheight - b.getHeight()));
                        } else {
                            IStyle parentStyle = MainActivity.workbook.getStyleSheet().findStyle(b.parent.topic.getStyleId());
                            Line l = new Line(parentStyle.getProperty(Styles.LineClass), Integer.parseInt(parentStyle.getProperty(Styles.LineWidth).substring(0,parentStyle.getProperty(Styles.LineWidth).length() - 2)), new ColorDrawable(Integer.parseInt(parentStyle.getProperty(Styles.LineColor))),
                                    new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY()),
                                    new Point(b.parent.getDrawableShape().getBounds().left - off * 50,LUheight - b.getHeight()), true);
                            l.off = 10;
                            b.parent.getLines().put(b, l);

                        }
                        int h = 0;
                        for (int i=0; i<b.getChildren().size(); i++) {
                            h += b.getChildren().get(i).getHeight() + 35;
                        }
                        LUheight -= (b.getHeight() + 20 + h);
                        UL = false;
                    } else {
                        b.point = new Point(b.parent.getDrawableShape().getBounds().left - off *  50  - b.getWidth() , LDHehight + 40);
                        if (b.parent.getLines().get(b) != null) {
                            b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()));
                            b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().right + 50,LDHehight + b.getHeight()));
                        } else {
                            IStyle parentStyle = MainActivity.workbook.getStyleSheet().findStyle(b.parent.topic.getStyleId());
                            Line l = new Line(parentStyle.getProperty(Styles.LineClass), Integer.parseInt(parentStyle.getProperty(Styles.LineWidth).substring(0,parentStyle.getProperty(Styles.LineWidth).length() - 2)), new ColorDrawable(Integer.parseInt(parentStyle.getProperty(Styles.LineColor))),
                                    new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY()),
                                    new Point(b.parent.getDrawableShape().getBounds().left - off * 50,LDHehight + b.getHeight()/2), true);
                            b.parent.getLines().put(b, l);

                        }
                        int h = 0;
                        for (int i=0; i<b.getChildren().size(); i++) {
                            h += b.getChildren().get(i).getHeight() + 35;
                        }
                        LDHehight += b.getHeight() + 20 + h;
                        UL = true;
                    }
                }
                b.position = Position.LFET;
                left--;
            }
        } else {
            if (b.parent.drawableShape.getBounds().left <= MainActivity.root.drawableShape.getBounds().centerX()) {
               // b.position = Position.LFET;
                if (b.parent.getChildren().size() == 1) {
                    b.point = new Point(b.parent.getDrawableShape().getBounds().left - 20 - b.getWidth(), b.parent.getDrawableShape().getBounds().centerY() - b.getHeight()/2);
                    if (b.parent.getLines().get(b) != null) {
                        b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY()));
                        b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().left - 20 - b.getWidth(), b.parent.getDrawableShape().getBounds().centerY()));
                    } else {
                        IStyle parentStyle = MainActivity.workbook.getStyleSheet().findStyle(b.parent.topic.getStyleId());
                        Line l = new Line(parentStyle.getProperty(Styles.LineClass), Integer.parseInt(parentStyle.getProperty(Styles.LineWidth).substring(0, parentStyle.getProperty(Styles.LineWidth).length() - 2)), new ColorDrawable(Integer.parseInt(parentStyle.getProperty(Styles.LineColor))),
                                new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY()),
                                new Point(b.parent.getDrawableShape().getBounds().left - 20 - b.getWidth(), b.parent.getDrawableShape().getBounds().centerY()), true);
                        l.off = 10;
                        b.parent.getLines().put(b, l);

                    }
                } else {
                    int h = 0;
                    int start = 0;
                    int fStart = 0;
                    for (int i = 0; i<b.parent.getChildren().size(); i++) {
                        if (b.parent.getChildren().equals(b)) {
                            start = h;
                        }
                       h += b.parent.getChildren().get(i).getHeight() + 35;
                    }
                    fStart = b.parent.getDrawableShape().getBounds().centerY() - h/2;
                    b.point = new Point(b.parent.getDrawableShape().getBounds().left - 20 - b.getWidth(), fStart + start);
                    if (b.parent.getLines().get(b) != null) {
                        b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY()));
                        b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().left - 20, fStart + h/2));
                    } else {
                        IStyle parentStyle = MainActivity.workbook.getStyleSheet().findStyle(b.parent.topic.getStyleId());
                        Line l = new Line(parentStyle.getProperty(Styles.LineClass), Integer.parseInt(parentStyle.getProperty(Styles.LineWidth).substring(0, parentStyle.getProperty(Styles.LineWidth).length() - 2)), new ColorDrawable(Integer.parseInt(parentStyle.getProperty(Styles.LineColor))),
                                new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY()),
                                new Point(b.parent.getDrawableShape().getBounds().left - 20 - b.getWidth(), fStart + h/2), true);
                        l.off = 10;
                        b.parent.getLines().put(b, l);

                    }
                }
            } else  {
            //    b.position = Position.RIGHT;
                if (b.parent.getChildren().size() == 1) {
                    b.point = new Point(b.parent.getDrawableShape().getBounds().right + 20, b.parent.getDrawableShape().getBounds().centerY() - b.getHeight()/2);
                    if (b.parent.getLines().get(b) != null) {
                        b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()));
                        b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().right + 20, b.parent.getDrawableShape().getBounds().centerY()));
                    } else {
                        IStyle parentStyle = MainActivity.workbook.getStyleSheet().findStyle(b.parent.topic.getStyleId());
                        Line l = new Line(parentStyle.getProperty(Styles.LineClass), Integer.parseInt(parentStyle.getProperty(Styles.LineWidth).substring(0, parentStyle.getProperty(Styles.LineWidth).length() - 2)), new ColorDrawable(Integer.parseInt(parentStyle.getProperty(Styles.LineColor))),
                                new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()),
                                new Point(b.parent.getDrawableShape().getBounds().right + 20 , b.parent.getDrawableShape().getBounds().centerY()), true);
                        l.off = 10;
                        b.parent.getLines().put(b, l);

                    }
                } else {
                    int h = 0;
                    int start = 0;
                    int fStart = 0;
                    for (int i = 0; i<b.parent.getChildren().size(); i++) {
                        if (b.parent.getChildren().get(i).compareTo(b) == 0) {
                            start = h;
                        }
                        h += b.parent.getChildren().get(i).getHeight() + 35;
                    }
                    fStart = b.parent.getDrawableShape().getBounds().centerY() - h/2;
                    b.point = new Point(b.parent.getDrawableShape().getBounds().right + 20, fStart + start);
                    if (b.parent.getLines().get(b) != null) {
                        b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()));
                        b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().right + 20, fStart + h/2));
                    } else {
                        IStyle parentStyle = MainActivity.workbook.getStyleSheet().findStyle(b.parent.topic.getStyleId());
                        Line l = new Line(parentStyle.getProperty(Styles.LineClass), Integer.parseInt(parentStyle.getProperty(Styles.LineWidth).substring(0, parentStyle.getProperty(Styles.LineWidth).length() - 2)), new ColorDrawable(Integer.parseInt(parentStyle.getProperty(Styles.LineColor))),
                                new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()),
                                new Point(b.parent.getDrawableShape().getBounds().right + 20, fStart + h/2), true);
                        l.off = 10;
                        b.parent.getLines().put(b, l);

                    }
                }
            }
        }
    }

    public void drawRelationship(Box box) {
        Path path = new Path();
        path.moveTo(box.getDrawableShape().getBounds().left, box.getDrawableShape().getBounds().centerY());
        path.lineTo(box.related.getDrawableShape().getBounds().right, box.related.getDrawableShape().getBounds().centerY());
//        path.lineTo(box.related.getDrawableShape().getBounds().right - 20, box.related.getDrawableShape().getBounds().centerY() - 10);
//        path.lineTo(box.related.getDrawableShape().getBounds().right - 20, box.related.getDrawableShape().getBounds().centerY() + 10);
//        path.lineTo(box.related.getDrawableShape().getBounds().right, box.related.getDrawableShape().getBounds().centerY());
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        // Stamp a concave arrow along the line
        PathEffect effect = new PathDashPathEffect(
                makeConvexArrow(24.0f, 14.0f),    // "stamp"
                36.0f,                            // advance, or distance between two stamps
                0.0f,                             // phase, or offset before the first stamp
                PathDashPathEffect.Style.ROTATE); // how to transform each stamp

// Apply the effect and draw the path
        paint.setPathEffect(effect);
        canvas.drawPath(path, paint);
        Paint paint1 = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(13);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawTextOnPath("relationhip", path, 20, 20, paint1);
    }

    private Path makeConvexArrow(float length, float height) {
        Path p = new Path();
        p.moveTo(0.0f, -height / 4.0f);
       // p.lineTo(length - height / 8.0f, -height / 4.0f);
        p.lineTo(length, 0.0f);
        //p.lineTo(length - height / 8.0f, height / 4.0f);
        p.lineTo(0.0f, height / 4.0f);
        p.lineTo(0.0f + height / 8.0f, 0.0f);
        p.close();
        return p;
    }
}