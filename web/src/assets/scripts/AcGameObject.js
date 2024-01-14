const AC_GAME_OBJECT = [];

export class AcGameObject{ //基类
    constructor(){
        AC_GAME_OBJECT.push(this);
        this.timedelta = 0; //两帧之间的时间差
        this.has_called_start = false; 
    }

    start(){//只会执行一次

    }

    update(){//每一帧执行一次，除了第一帧以外

    }

    on_destory(){//删除之前执行

    }

    destory(){
        this.on_destory();

        for(let i in AC_GAME_OBJECT){//in遍历的是下标
            const obj = AC_GAME_OBJECT[i];
            if(obj == this){
                AC_GAME_OBJECT.splice(i);//删除
                break;
            }
        }
    }
}

let last_timestamp; //上一次执行的时刻
const step = timestamp => { //这个函数每秒被调用60次，timestamp是回调函数step接收的时间戳参数
    for(let obj of AC_GAME_OBJECT){ //of遍历的是值
        if(!obj.has_called_start){
            obj.has_called_start = true;
            obj.start();
        }
        else {
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }

    last_timestamp = timestamp;
    requestAnimationFrame(step) //一般写法，记住好了
}

requestAnimationFrame(step) //当将requestAnimationFrame(step)放在函数外面时，
//如果这个 JavaScript 文件被加载和执行，则requestAnimationFrame()会自动开始循环执行传入的回调函数step()。