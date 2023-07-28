import {Document, model, Schema} from "mongoose";
import {IBase, LogicDelete, schemaConfig, TBase} from "src/models/base";

export type TUserWidget = {
    userId: string
    title: string,
    desc: string,
    html: string,
} & TBase;

/**
 * Mongoose Document based on TUser for TypeScript.
 * https://mongoosejs.com/docs/documents.html
 *
 * TUser
 * @param email:string
 * @param password:string
 * @param avatar:string
 */

export interface IUserWidget extends TUserWidget, Document {
}

const userWidgetSchema: Schema = new Schema(Object.assign({
    userId: {
        type: String,
        required: true,
    },
    title: {
        type: String,
        required: true,
    },
    desc: {
        type: String,
        require: true,
    },
    html: {
        type: String,
    }
}, IBase), schemaConfig);

/**
 * Mongoose Model based on TUser for TypeScript.
 * https://mongoosejs.com/docs/models.html
 *
 * TUser
 * @param email:string
 * @param password:string
 * @param avatar:string
 */

const UserWidget = model<IUserWidget, LogicDelete<IUserWidget, any>>("UserWidget", userWidgetSchema);

export default UserWidget;
